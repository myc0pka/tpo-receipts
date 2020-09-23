package lab1.service

import lab1.db.Consumptions
import lab1.db.Persons
import lab1.db.ReceiptItems
import lab1.db.Receipts
import lab1.model.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*

class ReceiptRepositoryTest {

    companion object {

        @JvmStatic
        @BeforeAll
        fun dbConnect() {
            Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", user = "sa", password = "")
        }

        private const val OWNER_TOKEN = "adf32e7fd"

        private const val SAVED_RECEIPT_ID = 1
        private const val SAVED_RECEIPT_NAME = "Saved Receipt"
        private const val SAVED_RECEIPT_TOTAL_SUM = 100.0

        private const val PERSON1_NAME = "Person 1"
        private const val PERSON2_NAME = "Person 2"

        private const val ITEM1_NAME = "Item 1"
        private const val ITEM1_AMOUNT = 4
        private const val ITEM1_PRICE = 100.0
        private const val ITEM2_NAME = "Item 2"
        private const val ITEM2_AMOUNT = 4
        private const val ITEM2_PRICE = 200.0

        private const val RECEIPT2_TOTAL_SUM = 200.0
        private const val RECEIPT_AVERAGE_SUM = 150.0
    }

    @BeforeEach
    fun createTables() {
        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(Receipts, Persons, ReceiptItems, Consumptions)
        }
    }

    @AfterEach
    fun dropTables() {
        transaction { SchemaUtils.drop(Receipts, Persons, ReceiptItems, Consumptions) }
    }

    @Nested
    inner class WithOneSavedReceipt {

        private fun insertPerson(receiptId: EntityID<Int>, personName: String): EntityID<Int> {
            return Persons.insert {
                it[receipt] = receiptId
                it[name] = personName
            } get Persons.id
        }

        private fun insertItem(
            receiptId: EntityID<Int>,
            itemName: String,
            itemAmount: Int,
            itemPrice: Double
        ): EntityID<Int> {
            return ReceiptItems.insert {
                it[receipt] = receiptId
                it[name] = itemName
                it[amount] = itemAmount
                it[price] = itemPrice
            } get ReceiptItems.id
        }

        @Suppress("SameParameterValue")
        private fun insertConsumption(
            receiptId: EntityID<Int>,
            personId: EntityID<Int>,
            itemId: EntityID<Int>,
            consumedAmount: Int
        ) {
            Consumptions.insert {
                it[receipt] = receiptId
                it[person] = personId
                it[item] = itemId
                it[amount] = consumedAmount
            }
        }

        @BeforeEach
        fun setUp() {
            transaction {
                val receiptId = Receipts.insert {
                    it[id] = EntityID(SAVED_RECEIPT_ID, Receipts)
                    it[ownerToken] = OWNER_TOKEN
                    it[name] = SAVED_RECEIPT_NAME
                    it[totalSum] = SAVED_RECEIPT_TOTAL_SUM
                } get Receipts.id

                val person1Id = insertPerson(receiptId, PERSON1_NAME)
                val person2Id = insertPerson(receiptId, PERSON2_NAME)

                val item1Id = insertItem(receiptId, ITEM1_NAME, ITEM1_AMOUNT, ITEM1_PRICE)
                val item2Id = insertItem(receiptId, ITEM2_NAME, ITEM2_AMOUNT, ITEM2_PRICE)

                insertConsumption(receiptId, person1Id, item1Id, ITEM1_AMOUNT / 2)
                insertConsumption(receiptId, person1Id, item2Id, ITEM2_AMOUNT / 2)
                insertConsumption(receiptId, person2Id, item1Id, ITEM1_AMOUNT / 2)
                insertConsumption(receiptId, person2Id, item2Id, ITEM2_AMOUNT / 2)
            }
        }

        @Test
        fun getReceiptNamesByToken() {
            Assertions.assertEquals(
                listOf(SAVED_RECEIPT_NAME),
                ReceiptRepository.getReceiptNamesByToken(OWNER_TOKEN).map { it.name }
            )
        }

        @Test
        fun getPersonTotalConsumption() {
            Assertions.assertEquals(
                listOf(
                    PersonTotalConsumption(
                        PERSON1_NAME,
                        ITEM1_PRICE * (ITEM1_AMOUNT / 2) + ITEM2_PRICE * (ITEM2_AMOUNT / 2)
                    ),
                    PersonTotalConsumption(
                        PERSON2_NAME,
                        ITEM1_PRICE * (ITEM1_AMOUNT / 2) + ITEM2_PRICE * (ITEM2_AMOUNT / 2)
                    )
                ),
                ReceiptRepository.getPersonTotalConsumptions(SAVED_RECEIPT_ID).sortedBy { it.personName }
            )
        }

        @Test
        fun getReceiptTotalSum() {
            Assertions.assertEquals(SAVED_RECEIPT_TOTAL_SUM, ReceiptRepository.getReceiptTotalSum(SAVED_RECEIPT_ID))
        }

        @Test
        fun getReceiptItems() {
            Assertions.assertEquals(
                listOf(
                    ReceiptItem(ITEM1_NAME, ITEM1_AMOUNT, ITEM1_PRICE),
                    ReceiptItem(ITEM2_NAME, ITEM2_AMOUNT, ITEM2_PRICE)
                ),
                ReceiptRepository.getReceiptItems(SAVED_RECEIPT_ID).sortedBy { it.name }
            )
        }

        @Test
        fun deleteReceipt() {
            ReceiptRepository.deleteReceipt(SAVED_RECEIPT_ID)
            Assertions.assertNull(transaction {
                Receipts.select { Receipts.id eq EntityID(SAVED_RECEIPT_ID, Receipts) }.singleOrNull()
            })
        }

        @Test
        fun getAverageSumByToken() {
            transaction {
                Receipts.insert {
                    it[ownerToken] = OWNER_TOKEN
                    it[name] = "Receipt 2"
                    it[totalSum] = RECEIPT2_TOTAL_SUM
                }
            }
            Assertions.assertEquals(RECEIPT_AVERAGE_SUM, ReceiptRepository.getAverageSumByToken(OWNER_TOKEN))
        }
    }

    @Nested
    inner class WithNoSavedReceipts {

        @Test
        fun getAverageSumByToken() {
            Assertions.assertEquals(0.0, ReceiptRepository.getAverageSumByToken(OWNER_TOKEN))
        }

        @Test
        fun saveReceipt() {
            val persons = listOf(Person(PERSON1_NAME), Person(PERSON2_NAME))
            val items = listOf(
                ReceiptItem(ITEM1_NAME, ITEM1_AMOUNT, ITEM1_PRICE),
                ReceiptItem(ITEM2_NAME, ITEM2_AMOUNT, ITEM2_PRICE)
            )
            ReceiptRepository.saveReceipt(
                receipt = Receipt(
                    name = SAVED_RECEIPT_NAME,
                    totalSum = SAVED_RECEIPT_TOTAL_SUM,
                    persons = persons,
                    items = items,
                    consumptions = listOf(
                        Consumption(items[0], persons[1], ITEM1_AMOUNT),
                        Consumption(items[1], persons[0], ITEM2_AMOUNT)
                    )
                ),
                ownerToken = OWNER_TOKEN
            )

            transaction {
                val receiptQuery = Receipts.selectAll().single()
                val receiptId = receiptQuery[Receipts.id]
                Assertions.assertEquals(SAVED_RECEIPT_NAME, receiptQuery[Receipts.name])
                Assertions.assertEquals(SAVED_RECEIPT_TOTAL_SUM, receiptQuery[Receipts.totalSum])
                Assertions.assertEquals(OWNER_TOKEN, receiptQuery[Receipts.ownerToken])

                val personsQuery = Persons.select { Persons.receipt eq receiptId }.sortedBy { it[Persons.name] }
                val person1Id = personsQuery[0][Persons.id]
                val person2Id = personsQuery[1][Persons.id]
                Assertions.assertEquals(PERSON1_NAME, personsQuery[0][Persons.name])
                Assertions.assertEquals(PERSON2_NAME, personsQuery[1][Persons.name])

                val itemsQuery =
                    ReceiptItems.select { ReceiptItems.receipt eq receiptId }.sortedBy { it[ReceiptItems.name] }
                val item1Id = itemsQuery[0][ReceiptItems.id]
                val item2Id = itemsQuery[1][ReceiptItems.id]
                Assertions.assertEquals(ITEM1_NAME, itemsQuery[0][ReceiptItems.name])
                Assertions.assertEquals(ITEM1_AMOUNT, itemsQuery[0][ReceiptItems.amount])
                Assertions.assertEquals(ITEM1_PRICE, itemsQuery[0][ReceiptItems.price])
                Assertions.assertEquals(ITEM2_NAME, itemsQuery[1][ReceiptItems.name])
                Assertions.assertEquals(ITEM2_AMOUNT, itemsQuery[1][ReceiptItems.amount])
                Assertions.assertEquals(ITEM2_PRICE, itemsQuery[1][ReceiptItems.price])

                val consumptionsQuery = Consumptions.select { Consumptions.receipt eq receiptId }
                val person1Consumption = consumptionsQuery.find { it[Consumptions.person] == person1Id }!!
                Assertions.assertEquals(item2Id, person1Consumption[Consumptions.item])
                Assertions.assertEquals(ITEM2_AMOUNT, person1Consumption[Consumptions.amount])
                val person2Consumption = consumptionsQuery.find { it[Consumptions.person] == person2Id }!!
                Assertions.assertEquals(item1Id, person2Consumption[Consumptions.item])
                Assertions.assertEquals(ITEM1_AMOUNT, person2Consumption[Consumptions.amount])
            }
        }
    }
}
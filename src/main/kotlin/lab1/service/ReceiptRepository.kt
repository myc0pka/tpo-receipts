package lab1.service

import lab1.db.Consumptions
import lab1.db.Persons
import lab1.db.ReceiptItems
import lab1.db.Receipts
import lab1.model.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.avg
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object ReceiptRepository {

    fun saveReceipt(receipt: Receipt, ownerToken: String) {
        transaction {
            val generatedReceiptId = Receipts.insert {
                it[this.ownerToken] = ownerToken
                it[name] = receipt.name
                it[totalSum] = receipt.totalSum
            } get Receipts.id
            val personsWithId = receipt.persons.map { person ->
                val generatedId = Persons.insert {
                    it[name] = person.name
                    it[this.receipt] = generatedReceiptId
                } get Persons.id
                person.copy(id = generatedId.value)
            }
            val itemsWithId = receipt.items.map { item ->
                val generatedId = ReceiptItems.insert {
                    it[name] = item.name
                    it[amount] = item.amount
                    it[price] = item.price
                    it[this.receipt] = generatedReceiptId
                } get ReceiptItems.id
                item.copy(id = generatedId.value)
            }
            receipt.consumptions.forEach { consumption ->
                val personIdValue = personsWithId.find { it.name == consumption.person.name }!!.id
                val itemIdValue = itemsWithId.find { it.name == consumption.item.name }!!.id
                Consumptions.insert {
                    it[this.receipt] = generatedReceiptId
                    it[person] = EntityID(personIdValue, Persons)
                    it[item] = EntityID(itemIdValue, ReceiptItems)
                    it[amount] = consumption.amount
                }
            }
        }
    }

    fun getReceiptNamesByToken(ownerToken: String): List<NamedReceipt> {
        return transaction {
            Receipts
                .slice(Receipts.id, Receipts.name)
                .select { Receipts.ownerToken eq ownerToken }
                .map { NamedReceipt(it[Receipts.name], it[Receipts.id].value) }
        }
    }

    private val Int.receiptEntityId: EntityID<Int>
        get() = EntityID(this, Receipts)

    fun getPersonTotalConsumptions(receiptId: Int): List<PersonTotalConsumption> {
        return transaction {
            (Consumptions innerJoin Persons innerJoin ReceiptItems)
                .slice(Persons.name, Consumptions.amount, ReceiptItems.price)
                .select { Consumptions.receipt eq receiptId.receiptEntityId }
                .groupBy { it[Persons.name] }
                .map { (personName, resultRowList) ->
                    PersonTotalConsumption(
                        personName,
                        totalConsumption = resultRowList.fold(0.0) { acc, resultRow ->
                            acc + resultRow[Consumptions.amount] * resultRow[ReceiptItems.price]
                        }
                    )
                }
        }
    }

    fun getReceiptTotalSum(receiptId: Int): Double {
        return transaction {
            Receipts
                .slice(Receipts.totalSum)
                .select { Receipts.id eq receiptId.receiptEntityId }
                .single()[Receipts.totalSum]
        }
    }

    fun getReceiptItems(receiptId: Int): List<ReceiptItem> {
        return transaction {
            ReceiptItems
                .select { ReceiptItems.receipt eq receiptId.receiptEntityId }
                .map {
                    ReceiptItem(it[ReceiptItems.name], it[ReceiptItems.amount], it[ReceiptItems.price])
                }
        }
    }

    fun deleteReceipt(receiptId: Int) {
        transaction {
            Receipts.deleteWhere { Receipts.id eq receiptId.receiptEntityId }
        }
    }

    fun getAverageSumByToken(token: String): Double {
        return transaction {
            Receipts
                .slice(Receipts.totalSum.avg())
                .select { Receipts.ownerToken eq token }
                .groupBy(Receipts.ownerToken)
                .singleOrNull()?.get(Receipts.totalSum.avg())?.toDouble() ?: 0.0
        }
    }
}
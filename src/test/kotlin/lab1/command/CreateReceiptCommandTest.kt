package lab1.command

import io.mockk.every
import io.mockk.mockkObject
import io.mockk.verify
import lab1.model.*
import lab1.service.ReceiptRepository
import lab1.service.TokenService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

private const val LOCAL_TOKEN = "fash2refisdl"

class CreateReceiptCommandTest {

    companion object {

        private const val RECEIPT_NAME = "Name"

        private const val PERSON1_NAME = "Person1"
        private val PERSON1 = Person(PERSON1_NAME)
        private const val PERSON2_NAME = "Person2"
        private val PERSON2 = Person(PERSON2_NAME)

        private const val ITEM1_NAME = "Item1"
        private const val ITEM1_AMOUNT = 2
        private const val ITEM1_PRICE = 100.0
        private val ITEM1 = ReceiptItem(ITEM1_NAME, ITEM1_AMOUNT, ITEM1_PRICE)
        private const val ITEM2_NAME = "Item2"
        private const val ITEM2_AMOUNT = 3
        private const val ITEM2_PRICE = 200.0
        private val ITEM2 = ReceiptItem(ITEM2_NAME, ITEM2_AMOUNT, ITEM2_PRICE)

        private const val TOTAL_SUM = ITEM1_AMOUNT * ITEM1_PRICE + ITEM2_AMOUNT * ITEM2_PRICE

        private val CONSUMPTION1 = Consumption(ITEM1, PERSON1, 1)
        private val CONSUMPTION2 = Consumption(ITEM2, PERSON2, 2)
    }

    private val command = CreateReceiptCommand(LOCAL_TOKEN)

    @Test
    @DisplayName("execute() should call ReceiptRepository#saveReceipt()")
    fun execute() {
        mockkObject(TokenService)
        every { TokenService.getLocalToken() } returns LOCAL_TOKEN
        mockkObject(ReceiptRepository)
        every { ReceiptRepository.saveReceipt(any(), any()) } returns Unit

        command.apply {
            name = RECEIPT_NAME
            addPerson(PERSON1)
            addPerson(PERSON2)
            addItem(ITEM1)
            addItem(ITEM2)
            addConsumption(CONSUMPTION1)
            addConsumption(CONSUMPTION2)

            execute()
        }
        verify {
            ReceiptRepository.saveReceipt(
                Receipt(
                    name = RECEIPT_NAME,
                    totalSum = TOTAL_SUM,
                    persons = listOf(PERSON1, PERSON2),
                    items = listOf(ITEM1, ITEM2),
                    consumptions = listOf(CONSUMPTION1, CONSUMPTION2)
                ),
                LOCAL_TOKEN
            )
        }
    }

    @Test
    @DisplayName("When there are no people with given name hasPersonWithName() should return false")
    fun hasPersonWithName_NoPeopleWithSuchName() {
        command.addPerson(Person("Name1"))
        Assertions.assertFalse(command.hasPersonWithName("Name2"))
    }

    @Test
    @DisplayName("When there is a person who has given name hasPersonWithName() should return true")
    fun hasPersonWithName_OnePersonHasSuchName() {
        command.addPerson(Person("Name"))
        Assertions.assertTrue(command.hasPersonWithName("Name"))
    }

    @Test
    @DisplayName("When there are no items with given name hasItemWithName() should return false")
    fun hasItemWithName_NoItemsWithSuchName() {
        command.addItem(ReceiptItem("Name 1", 0, 0.0))
        Assertions.assertFalse(command.hasItemWithName("Name 2"))
    }

    @Test
    @DisplayName("When there is an item that has given name hasItemWithName() should return true")
    fun hasItemWithName_OneItemHasSuchName() {
        command.addItem(ReceiptItem("Name", 0, 0.0))
        Assertions.assertTrue(command.hasItemWithName("Name"))
    }

    @Test
    @DisplayName("preview() should create and return ReceiptPreview instance")
    fun preview() {
        command.apply {
            name = RECEIPT_NAME
            addPerson(PERSON1)
            addPerson(PERSON2)
            addItem(ITEM1)
            addItem(ITEM2)
        }

        Assertions.assertEquals(
            ReceiptPreview(
                name = RECEIPT_NAME,
                personNames = listOf(PERSON1_NAME, PERSON2_NAME),
                itemSummaries = listOf(
                    "'$ITEM1_NAME' x$ITEM1_AMOUNT * $ITEM1_PRICE",
                    "'$ITEM2_NAME' x$ITEM2_AMOUNT * $ITEM2_PRICE"
                ),
                totalSum = TOTAL_SUM
            ),
            command.preview
        )
    }
}
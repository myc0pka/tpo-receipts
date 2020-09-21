package lab1.menu

import io.mockk.MockKAdditionalAnswerScope
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import lab1.command.CreateReceiptCommand
import lab1.model.Person
import lab1.model.ReceiptItem
import lab1.service.ConsoleIOService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private const val CONSUMED_ITEM_NAME = "Consumed Item"
private const val CONSUMED_ITEM_AMOUNT = 10
private const val CONSUMED_ITEM_PRICE = 2.5

private const val PERSON1_NAME = "Person 1"
private const val PERSON2_NAME = "Person 2"

class ReceiptConsumptionPageTest : BaseMenuPageTest() {

    private val createCommandMock = mockk<CreateReceiptCommand>(relaxed = true) {
        every { this@mockk.getProperty("persons") } returns listOf(Person(PERSON1_NAME), Person(PERSON2_NAME))
    }

    private val receiptConsumptionPage = ReceiptConsumptionPage(
        createReceiptCommand = createCommandMock,
        consumedItem = ReceiptItem(CONSUMED_ITEM_NAME, CONSUMED_ITEM_AMOUNT, CONSUMED_ITEM_PRICE),
        availableAmount = CONSUMED_ITEM_AMOUNT
    )

    @Test
    @DisplayName("On show should print title including item name and amount and options including persons' names")
    fun onShow() {
        every { ConsoleIOService.getInput() } returns "3"

        receiptConsumptionPage.show()
        verify {
            ConsoleIOService.print(
                message = "Кто покупал '$CONSUMED_ITEM_NAME'? (осталось $CONSUMED_ITEM_AMOUNT)",
                endLine = true
            )
        }
        verify { ConsoleIOService.print("1) $PERSON1_NAME", endLine = true) }
        verify { ConsoleIOService.print("2) $PERSON2_NAME", endLine = true) }
        verify { ConsoleIOService.print("3) Остальное брал я", endLine = true) }
        verify { ConsoleIOService.print("4) Вернуться в главное меню", endLine = true) }
    }

    @Nested
    @DisplayName("When person option selected")
    inner class PersonOptionsSelected {

        private val mockedOptionInput by lazy {
            every { ConsoleIOService.getInput() } returns "1"
        }

        private fun MockKAdditionalAnswerScope<String, String>.andThenValidAmount() {
            andThen("1")
        }

        @Test
        @DisplayName("show() should request consumed amount input")
        fun anyway() {
            mockedOptionInput.andThenValidAmount()

            receiptConsumptionPage.show()
            verify { ConsoleIOService.print("Введите количество: ", endLine = false) }
        }

        @Test
        @DisplayName("When empty amount input show() should print error message and request input again")
        fun emptyAmountInput() {
            mockedOptionInput.andThen("").andThenValidAmount()

            receiptConsumptionPage.show()
            verify { ConsoleIOService.print("Количество не должно быть пустым", endLine = true) }
        }

        @Test
        @DisplayName("When not integer amount input show() should print error message and request input again")
        fun notIntAmountInput() {
            mockedOptionInput.andThen("adf").andThenValidAmount()

            receiptConsumptionPage.show()
            verify { ConsoleIOService.print("Введите число от 0 до $CONSUMED_ITEM_AMOUNT", endLine = true) }
        }

        @Test
        @DisplayName("When zero amount input show() should print error message and request input again")
        fun zeroAmountInput() {
            mockedOptionInput.andThen("0").andThenValidAmount()

            receiptConsumptionPage.show()
            verify { ConsoleIOService.print("Введите число от 0 до $CONSUMED_ITEM_AMOUNT", endLine = true) }
        }

        @Test
        @DisplayName("When zero amount input show() should print error message and request input again")
        fun tooBigAmountInput() {
            mockedOptionInput.andThen((CONSUMED_ITEM_AMOUNT + 1).toString()).andThenValidAmount()

            receiptConsumptionPage.show()
            verify { ConsoleIOService.print("Введите число от 0 до $CONSUMED_ITEM_AMOUNT", endLine = true) }
        }

        @Test
        @DisplayName("When input amount equals to available show() should return ShowPage(ReceiptItemsPage)")
        fun inputAmountEqualsToAvailable() {
            mockedOptionInput.andThen(CONSUMED_ITEM_AMOUNT.toString())

            Assertions.assertEquals(
                ReceiptItemsPage::class,
                (receiptConsumptionPage.show() as Action.ShowPage).page::class
            )
        }

        @Test
        @DisplayName("When input amount is less than available show() should return ShowPage(ReceiptConsumptionPage)")
        fun inputAmountIsLessThanAvailable() {
            mockedOptionInput.andThen((CONSUMED_ITEM_AMOUNT - 1).toString())

            Assertions.assertEquals(
                ReceiptConsumptionPage::class,
                (receiptConsumptionPage.show() as Action.ShowPage).page::class
            )
        }
    }

    @Test
    @DisplayName("When rest is mine option selected show() should return ShowPage(ReceiptItemsPage)")
    fun restIsMineOptionSelected() {
        every { ConsoleIOService.getInput() } returns "3"

        Assertions.assertEquals(
            ReceiptItemsPage::class,
            (receiptConsumptionPage.show() as Action.ShowPage).page::class
        )
    }

    @Test
    @DisplayName("When go to main menu option selected show() should return ShowPage(MainMenuConfirmationPage)")
    fun goToMainMenuOptionSelected() {
        every { ConsoleIOService.getInput() } returns "4"

        Assertions.assertEquals(
            MainMenuConfirmationMenuPage::class,
            (receiptConsumptionPage.show() as Action.ShowPage).page::class
        )
    }
}
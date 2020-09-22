package lab1.menu

import io.mockk.every
import io.mockk.verify
import lab1.model.NamedReceipt
import lab1.model.PersonTotalConsumption
import lab1.model.ReceiptItem
import lab1.service.ConsoleIOService
import lab1.service.ReceiptRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

private const val RECEIPT_NAME = "Receipt Name"
private const val RECEIPT_ID = 1

class ReceiptPageTest : BaseMenuPageTest() {

    private val receiptPage = ReceiptPage(NamedReceipt(RECEIPT_NAME, RECEIPT_ID))

    @Test
    @DisplayName("On show should print title including receipt's name and print menu options")
    fun onShow() {
        every { ConsoleIOService.getInput() } returns "4"

        receiptPage.show()
        verify { ConsoleIOService.print("-- Чек '$RECEIPT_NAME' --", endLine = true) }
        verify { ConsoleIOService.print("1) Распечатать", endLine = true) }
        verify { ConsoleIOService.print("2) Показать суммы", endLine = true) }
        verify { ConsoleIOService.print("3) Удалить", endLine = true) }
        verify { ConsoleIOService.print("4) Вернуться в главное меню", endLine = true) }
    }

    @Test
    @DisplayName("When print option selected show() should print receipt's items and total sum and return ShowPage(this)")
    fun printSelected() {
        every {
            ReceiptRepository.getReceiptItems(RECEIPT_ID)
        } returns listOf(ReceiptItem("Item1", 2, 100.0), ReceiptItem("Item2", 3, 200.0))
        every { ReceiptRepository.getReceiptTotalSum(RECEIPT_ID) } returns 800.0

        every { ConsoleIOService.getInput() } returns "1"

        Assertions.assertEquals(receiptPage, (receiptPage.show() as Action.ShowPage).page)
        verify { ConsoleIOService.print("- Позиции в чеке '$RECEIPT_NAME' -", endLine = true) }
        verify { ConsoleIOService.print("1. Item1 2 шт. * 100.0", endLine = true) }
        verify { ConsoleIOService.print("2. Item2 3 шт. * 200.0", endLine = true) }
        verify { ConsoleIOService.print("-", endLine = true) }
        verify { ConsoleIOService.print("ИТОГО: 800.0", endLine = true) }
    }

    @Test
    @DisplayName("When show sums option selected show() should print every person's total sum, user's sum and total sum")
    fun showSumsSelected() {
        every {
            ReceiptRepository.getPersonTotalConsumptions(RECEIPT_ID)
        } returns listOf(PersonTotalConsumption("Person 1", 200.0), PersonTotalConsumption("Person 2", 300.0))
        every { ReceiptRepository.getReceiptTotalSum(RECEIPT_ID) } returns 600.0

        every { ConsoleIOService.getInput() } returns "2"

        Assertions.assertEquals(receiptPage, (receiptPage.show() as Action.ShowPage).page)
        verify { ConsoleIOService.print("- Суммы чека '$RECEIPT_NAME' -", endLine = true) }
        verify { ConsoleIOService.print("Person 1 должен вам 200.0", endLine = true) }
        verify { ConsoleIOService.print("Person 2 должен вам 300.0", endLine = true) }
        verify { ConsoleIOService.print("С вас: 100.0", endLine = true) }
        verify { ConsoleIOService.print("-", endLine = true) }
        verify { ConsoleIOService.print("ИТОГО: 600.0", endLine = true) }
    }

    @Test
    @DisplayName("When delete option selected show() should return ShowPage(DeletionConfirmationPage)")
    fun deleteSelected() {
        every { ConsoleIOService.getInput() } returns "3"

        Assertions.assertEquals(
            DeletionConfirmationMenuPage::class,
            (receiptPage.show() as Action.ShowPage).page::class
        )
    }

    @Test
    @DisplayName("When go to main menu selected show() should return ShowPage(MainMenuPage)")
    fun goToMainMenuSelected() {
        every { ConsoleIOService.getInput() } returns "4"

        Assertions.assertEquals(
            MainMenuPage::class,
            (receiptPage.show() as Action.ShowPage).page::class
        )
    }
}
package lab1.menu

import io.mockk.every
import io.mockk.verify
import lab1.model.NamedReceipt
import lab1.service.ConsoleIOService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class MyReceiptsPageTest : BaseMenuPageTest() {

    companion object {

        private const val RECEIPT1_NAME = "Receipt 1"
        private const val RECEIPT2_NAME = "Receipt 2"

        private val NAMED_RECEIPTS = listOf(NamedReceipt(RECEIPT1_NAME, id = 1), NamedReceipt(RECEIPT2_NAME, id = 2))
    }

    private val myReceiptsPage = MyReceiptsPage(NAMED_RECEIPTS)

    @Test
    @DisplayName("On show should print title and options including user's receipts' names")
    fun onShow() {
        every { ConsoleIOService.getInput() } returns "1"

        myReceiptsPage.show()
        verify { ConsoleIOService.print("-- Мои чеки --", endLine = true) }
        verify { ConsoleIOService.print("1) $RECEIPT1_NAME", endLine = true) }
        verify { ConsoleIOService.print("2) $RECEIPT2_NAME", endLine = true) }
        verify { ConsoleIOService.print("3) Вернуться в главное меню", endLine = true) }
    }

    @Test
    @DisplayName("When receipt options selected show() should return ShowPage(ReceiptPage)")
    fun receiptOptionSelected() {
        every { ConsoleIOService.getInput() } returns "1"

        Assertions.assertEquals(
            ReceiptPage::class,
            (myReceiptsPage.show() as Action.ShowPage).page::class
        )
    }

    @Test
    @DisplayName("When go to main menu option selected show() should return ShowPage(MainMenuPage)")
    fun goToMainMenuSelected() {
        every { ConsoleIOService.getInput() } returns "3"

        Assertions.assertEquals(
            MainMenuPage::class,
            (myReceiptsPage.show() as Action.ShowPage).page::class
        )
    }
}
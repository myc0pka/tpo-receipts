package lab1.menu

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import lab1.model.ReceiptName
import lab1.service.ConsoleIOService
import lab1.service.ReceiptRepository
import org.junit.jupiter.api.*

class DeletionConfirmationMenuPageTest : BaseMenuPageTest() {

    private val callingPageMock = mockk<ReceiptPage>(relaxed = true)
    private val targetReceiptId = 1
    private val deletionConfirmationMenuPage =
        DeletionConfirmationMenuPage(ReceiptName("", targetReceiptId), callingPageMock)

    @Nested
    @DisplayName("When `Yes` option selected")
    inner class YesOptionSelected {

        @BeforeEach
        fun setUp() {
            every { ConsoleIOService.getInput() } returns "1"
        }

        @Test
        @DisplayName("show() should print message, call ReceiptRepository#delete() and then return ShowPage(MainMenuPage)")
        fun anyway() {
            Assertions.assertEquals(
                MainMenuPage::class,
                (deletionConfirmationMenuPage.show() as Action.ShowPage).page::class
            )
            verify { ConsoleIOService.print("Удаление...", endLine = false) }
            verify { ReceiptRepository.deleteReceipt(targetReceiptId) }
        }

        @Test
        @DisplayName("When delete operation fails show() should print error message")
        fun deleteOperationFails() {
            every { ReceiptRepository.deleteReceipt(targetReceiptId) } throws Exception()
            deletionConfirmationMenuPage.show()
            verify { ConsoleIOService.print("Ошибка. Повторите попытку", endLine = true) }
        }

        @Test
        @DisplayName("When delete operation succeeds show() should print success message")
        fun deleteOperationSucceeds() {
            every { ReceiptRepository.deleteReceipt(targetReceiptId) } returns Unit
            deletionConfirmationMenuPage.show()
            verify { ConsoleIOService.print("Готово", endLine = true) }
        }
    }

    @Test
    @DisplayName("When `No` option selected show() return ShowPage(callingPage)")
    fun noSelected() {
        every { ConsoleIOService.getInput() } returns "2"
        Assertions.assertEquals(callingPageMock, (deletionConfirmationMenuPage.show() as Action.ShowPage).page)
    }
}
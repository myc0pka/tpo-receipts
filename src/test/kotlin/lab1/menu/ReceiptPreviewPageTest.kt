package lab1.menu

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import lab1.command.CreateReceiptCommand
import lab1.model.ReceiptPreview
import lab1.service.ConsoleIOService
import org.junit.jupiter.api.*

class ReceiptPreviewPageTest : BaseMenuPageTest() {

    companion object {

        private const val RECEIPT_NAME = "Receipt Name"

        private const val PERSON1_NAME = "Person 1"
        private const val PERSON2_NAME = "Person 2"

        private const val ITEM1_SUMMARY = "'Item1' x3 * 100.0"
        private const val ITEM2_SUMMARY = "'Item2' x4 * 200.0"

        private const val TOTAL_SUM = 1100.0

        private val RECEIPT_PREVIEW = ReceiptPreview(
            name = RECEIPT_NAME,
            personNames = listOf(PERSON1_NAME, PERSON2_NAME),
            itemSummaries = listOf(ITEM1_SUMMARY, ITEM2_SUMMARY),
            totalSum = TOTAL_SUM
        )
    }

    private val createCommandMock = mockk<CreateReceiptCommand>(relaxed = true) {
        every { this@mockk.getProperty("preview") } returns RECEIPT_PREVIEW
    }

    private val receiptPreviewPage = ReceiptPreviewPage(createCommandMock)

    @Test
    @DisplayName("On show should print receipt preview and show menu options")
    fun onShow() {
        every { ConsoleIOService.getInput() } returns "1"

        receiptPreviewPage.show()
        verify {
            ConsoleIOService.print(
                "Чек '$RECEIPT_NAME'\n" +
                        "Люди:\n" +
                        "\t- $PERSON1_NAME\n" +
                        "\t- $PERSON2_NAME\n" +
                        "Товары:\n" +
                        "\t- $ITEM1_SUMMARY\n" +
                        "\t- $ITEM2_SUMMARY\n" +
                        "ИТОГО: $TOTAL_SUM\n",
                endLine = true
            )
        }
        verify { ConsoleIOService.print("1) Создать чек", endLine = true) }
        verify { ConsoleIOService.print("2) Вернуться к добавлению товаров", endLine = true) }
        verify { ConsoleIOService.print("3) Вернуться в главное меню", endLine = true) }
    }

    @Nested
    @DisplayName("When create receipt option selected")
    inner class CreateReceiptSelected {

        @BeforeEach
        fun setUp() {
            every { ConsoleIOService.getInput() } returns "1"
        }

        @Test
        @DisplayName("Should print info message and execute the command")
        fun anyway() {
            receiptPreviewPage.show()

            verify { ConsoleIOService.print("Создание чека...", endLine = false) }
            verify { createCommandMock.execute() }
        }

        @Test
        @DisplayName("When create operation fails show() should print error message and return ShowPage(MainMenuPage)")
        fun operationFails() {
            every { createCommandMock.execute() } throws Exception()

            Assertions.assertEquals(
                MainMenuPage::class,
                (receiptPreviewPage.show() as Action.ShowPage).page::class
            )
            verify { ConsoleIOService.print("Ошибка. Повторите попытку", endLine = true) }
        }

        @Test
        @DisplayName("When create operation succeeds show() should print success message and return ShowPage(MainMenuPage)")
        fun operationSucceeds() {
            every { createCommandMock.execute() } returns Unit

            Assertions.assertEquals(
                MainMenuPage::class,
                (receiptPreviewPage.show() as Action.ShowPage).page::class
            )
            verify { ConsoleIOService.print("Готово", endLine = true) }
        }
    }

    @Test
    @DisplayName("When back to items option selected show() should return ShowPage(ReceiptItemsPage)")
    fun backToItemsSelected() {
        every { ConsoleIOService.getInput() } returns "2"

        Assertions.assertEquals(
            ReceiptItemsPage::class,
            (receiptPreviewPage.show() as Action.ShowPage).page::class
        )
    }

    @Test
    @DisplayName("When cancel option selected show() should return ShowPage(MainMenuConfirmationPage)")
    fun cancelSelected() {
        every { ConsoleIOService.getInput() } returns "3"

        Assertions.assertEquals(
            MainMenuConfirmationMenuPage::class,
            (receiptPreviewPage.show() as Action.ShowPage).page::class
        )
    }
}
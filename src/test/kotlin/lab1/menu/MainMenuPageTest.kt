package lab1.menu

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import lab1.service.ConsoleIOService
import lab1.service.ReceiptRepository
import lab1.service.TokenService
import org.junit.jupiter.api.*

class MainMenuPageTest : BaseMenuPageTest() {

    private val mainMenuPage = MainMenuPage()

    @Test
    @DisplayName("On show should print title and main menu options")
    fun onShow() {
        every { ConsoleIOService.getInput() } returns "1"
        mainMenuPage.show()
        verify { ConsoleIOService.print("-- Главное меню --", endLine = true) }
        verify { ConsoleIOService.print("1) Новый чек", endLine = true) }
        verify { ConsoleIOService.print("2) Мои чеки", endLine = true) }
        verify { ConsoleIOService.print("3) Показать среднюю сумму", endLine = true) }
        verify { ConsoleIOService.print("4) Выйти", endLine = true) }
    }

    @Test
    @DisplayName("When add receipt option selected show() should return ShowPage(NewReceiptPage)")
    fun addReceiptSelected() {
        every { ConsoleIOService.getInput() } returns "1"
        Assertions.assertEquals(
            NewReceiptPage::class,
            (mainMenuPage.show() as Action.ShowPage).page::class
        )
    }

    @Nested
    @DisplayName("When my receipts option selected")
    inner class MyReceiptsOptionSelected {

        @BeforeEach
        fun setUp() {
            every { ConsoleIOService.getInput() } returns "2"
        }

        @Test
        @DisplayName("When there are no receipts show() should print message and return ShowPage(MainMenuPage)")
        fun noReceipts() {
            every { ReceiptRepository.getReceiptNamesByToken(LOCAL_TOKEN) } returns emptyList()
            Assertions.assertEquals(
                MainMenuPage::class,
                (mainMenuPage.show() as Action.ShowPage).page::class
            )
            verify { ConsoleIOService.print("Пока что нет ни одного чека", endLine = true) }
        }

        @Test
        @DisplayName("When there are two receipts show() should return ShowPage(MyReceiptsPage)")
        fun twoReceipts() {
            every {
                ReceiptRepository.getReceiptNamesByToken(LOCAL_TOKEN)
            } returns listOf(mockk(relaxed = true), mockk(relaxed = true))
            Assertions.assertEquals(
                MyReceiptsPage::class,
                (mainMenuPage.show() as Action.ShowPage).page::class
            )
        }
    }

    @Test
    @DisplayName("When average sum option selected show() should print average sum and return ShowPage(this)")
    fun averageSumSelected() {
        val ownerToken = "da43rf"
        val averageSum = 123.45
        every { ConsoleIOService.getInput() } returns "3"
        every { TokenService.getLocalToken() } returns ownerToken
        every { ReceiptRepository.getAverageSumByToken(ownerToken) } returns averageSum

        Assertions.assertEquals(
            mainMenuPage,
            (mainMenuPage.show() as Action.ShowPage).page
        )
        verify { ConsoleIOService.print("Средняя сумма вашего чека: $averageSum", endLine = true) }
    }

    @Test
    @DisplayName("When exit option selected show() should return ShowPage(ExitConfirmationMenuPage)")
    fun exitSelected() {
        every { ConsoleIOService.getInput() } returns "4"
        Assertions.assertEquals(
            ExitConfirmationMenuPage::class,
            (mainMenuPage.show() as Action.ShowPage).page::class
        )
    }
}
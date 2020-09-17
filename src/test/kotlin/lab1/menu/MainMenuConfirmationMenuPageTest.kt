package lab1.menu

import io.mockk.every
import io.mockk.mockk
import lab1.service.ConsoleIOService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class MainMenuConfirmationMenuPageTest : BaseMenuPageTest() {

    private val callingPageMock = mockk<MenuPage>(relaxed = true)

    private val mainMenuConfirmationMenuPage = MainMenuConfirmationMenuPage(callingPageMock)

    @Test
    @DisplayName("When `Yes` option selected show() should return ShowPage(MainMenuPage)")
    fun yesSelected() {
        every { ConsoleIOService.getInput() } returns "1"
        Assertions.assertEquals(
            MainMenuPage::class,
            (mainMenuConfirmationMenuPage.show() as Action.ShowPage).page::class
        )
    }

    @Test
    @DisplayName("When `No` option selected show() should return ShowPage(callingPage)")
    fun noSelected() {
        every { ConsoleIOService.getInput() } returns "2"
        Assertions.assertEquals(callingPageMock, (mainMenuConfirmationMenuPage.show() as Action.ShowPage).page)
    }
}
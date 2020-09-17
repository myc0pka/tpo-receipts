package lab1.menu

import io.mockk.every
import lab1.service.ConsoleIOService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ExitConfirmationMenuPageTest : BaseMenuPageTest() {

    private val exitConfirmationMenuPage = ExitConfirmationMenuPage()

    @Test
    @DisplayName("When `Yes` option selected show() should return ExitProgram")
    fun yesSelected() {
        every { ConsoleIOService.getInput() } returns "1"
        Assertions.assertEquals(Action.ExitProgram, exitConfirmationMenuPage.show())
    }

    @Test
    @DisplayName("When `No` option selected show() should return ShowPage(MainMenuPage)")
    fun noSelected() {
        every { ConsoleIOService.getInput() } returns "2"
        Assertions.assertEquals(
            MainMenuPage::class,
            (exitConfirmationMenuPage.show() as Action.ShowPage).page::class
        )
    }
}
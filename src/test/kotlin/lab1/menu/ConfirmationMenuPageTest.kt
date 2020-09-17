package lab1.menu

import io.mockk.every
import io.mockk.verify
import lab1.service.ConsoleIOService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ConfirmationMenuPageTest : BaseMenuPageTest() {

    private class TestConfirmationMenuPage : ConfirmationMenuPage(title = "Title") {

        override fun onYesSelected(): Action {
            return Action.Stub("")
        }

        override fun onNoSelected(): Action {
            return Action.Stub("")
        }
    }

    private val confirmationMenuPage = TestConfirmationMenuPage()

    @Test
    @DisplayName("On show confirmation menu page should print title and 'Yes' and 'No' options")
    fun onShow() {
        every { ConsoleIOService.getInput() } returns "1"
        confirmationMenuPage.show()
        verify { ConsoleIOService.print("Title", endLine = true) }
        verify { ConsoleIOService.print("1) Да", endLine = true) }
        verify { ConsoleIOService.print("2) Нет", endLine = true) }
    }
}
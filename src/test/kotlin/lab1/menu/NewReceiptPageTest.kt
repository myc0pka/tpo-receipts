package lab1.menu

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import lab1.command.CreateReceiptCommand
import lab1.service.ConsoleIOService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class NewReceiptPageTest : BaseMenuPageTest() {

    private val createCommandMock = mockk<CreateReceiptCommand>(relaxed = true)
    private val newReceiptPage = NewReceiptPage(createCommandMock)

    @Test
    @DisplayName("On show should request receipt name input")
    fun onShow() {
        every { ConsoleIOService.getInput() } returns "a"
        newReceiptPage.show()
        verify { ConsoleIOService.print("Введите название чека: ", endLine = false) }
    }

    @Test
    @DisplayName("When empty name input should print error message and request input again")
    fun emptyNameInput() {
        every { ConsoleIOService.getInput() } returns "" andThen "a"
        newReceiptPage.show()
        verify { ConsoleIOService.print("Название не должно быть пустым", endLine = true) }
        verify(exactly = 0) { createCommandMock setProperty "name" value "" }
    }

    @Test
    @DisplayName("When too long name input should cut it")
    fun tooLongNameInput() {
        val tooLongName = buildString {
            repeat(21) { append('a') }
        }
        val cutName = buildString {
            repeat(20) { append('a') }
        }
        every { ConsoleIOService.getInput() } returns tooLongName
        newReceiptPage.show()
        verify { createCommandMock setProperty "name" value cutName }
    }

    @Test
    @DisplayName("When valid name input show() should return ShowPage(ReceiptPeoplePage)")
    fun validNameInput() {
        every { ConsoleIOService.getInput() } returns "abc"
        Assertions.assertEquals(
            ReceiptPeoplePage::class,
            (newReceiptPage.show() as Action.ShowPage).page::class
        )
        verify { createCommandMock setProperty "name" value "abc" }
    }
}
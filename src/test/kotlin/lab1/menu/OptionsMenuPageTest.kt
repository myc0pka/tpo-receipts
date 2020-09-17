package lab1.menu

import io.mockk.every
import io.mockk.verify
import lab1.service.ConsoleIOService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class OptionsMenuPageTest : BaseMenuPageTest() {

    private class TestOptionsMenuPage : OptionsMenuPage<TestOptionsMenuPage.Option>(
        title = "Title",
        options = Option.values().toList()
    ) {

        enum class Option(override val text: String) : MenuOption {

            FIRST_OPTION(text = "Option 1"),
            SECOND_OPTION(text = "Option 2")
        }

        override fun handleOptionInput(option: Option): Action {
            return when (option) {
                Option.FIRST_OPTION -> Action.Stub("1")
                Option.SECOND_OPTION -> Action.Stub("2")
            }
        }
    }

    private val optionsMenuPage = TestOptionsMenuPage()

    @Test
    @DisplayName("On show should print title, options and request option number input")
    fun show() {
        every { ConsoleIOService.getInput() } returns "1"
        optionsMenuPage.show()
        verify { ConsoleIOService.print("Title", endLine = true) }
        verify { ConsoleIOService.print("1) Option 1", endLine = true) }
        verify { ConsoleIOService.print("2) Option 2", endLine = true) }
        verify { ConsoleIOService.print("Выберите опцию (1-2): ", endLine = false) }
    }

    @Test
    @DisplayName("When user inputs not positive integer value show() should print error message and request again")
    fun onInput_NotPositiveIntegerValue() {
        every { ConsoleIOService.getInput() } returns "adf" andThen "1"
        optionsMenuPage.show()
        verify { ConsoleIOService.print("Некорректный ввод : 'adf'", endLine = true) }
    }

    @Test
    @DisplayName("When user inputs integer value out of range show() should print error message and request again")
    fun onInput_IntegerOutOfRange() {
        every { ConsoleIOService.getInput() } returns "3" andThen "1"
        optionsMenuPage.show()
        verify { ConsoleIOService.print("Некорректный номер : 3", endLine = true) }
    }

    @ParameterizedTest
    @ValueSource(strings = ["1", "2"])
    @DisplayName("When user inputs valid integer show() should perform corresponding actions")
    fun onInput_ValidInteger(validInteger: String) {
        every { ConsoleIOService.getInput() } returns validInteger
        Assertions.assertEquals(validInteger, (optionsMenuPage.show() as Action.Stub).message)
    }
}
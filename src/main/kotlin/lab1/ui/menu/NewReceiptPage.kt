package lab1.ui.menu

import lab1.command.CreateReceiptCommand
import kotlin.math.min

private const val NAME_MAX_LENGTH = 20

class NewReceiptPage : MenuPage() {

    private val command = CreateReceiptCommand()

    override fun show(): Action {
        val nameInput = requestNotEmptyInput(
            message = "Введите название чека: ",
            emptyInputMessage = "Название не должно быть пустым"
        )
        command.name = nameInput.substring(0, min(nameInput.length, NAME_MAX_LENGTH))
        return Action.ShowPage(ReceiptPeoplePage(command))
    }
}
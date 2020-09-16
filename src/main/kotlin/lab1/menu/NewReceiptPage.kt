package lab1.menu

import lab1.command.CreateReceiptCommand
import lab1.service.TokenService
import kotlin.math.min

const val RECEIPT_NAME_MAX_LENGTH = 20

class NewReceiptPage : MenuPage() {

    private val command = CreateReceiptCommand(ownerToken = TokenService.getLocalToken())

    override fun show(): Action {
        val nameInput = requestNotEmptyInput(
            message = "Введите название чека: ",
            emptyInputMessage = "Название не должно быть пустым"
        )
        command.name = nameInput.substring(0, min(nameInput.length, RECEIPT_NAME_MAX_LENGTH))
        return Action.ShowPage(ReceiptPeoplePage(command))
    }
}
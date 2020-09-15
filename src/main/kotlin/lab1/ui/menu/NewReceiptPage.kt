package lab1.ui.menu

import lab1.command.CreateReceiptCommand

private const val NAME_MAX_LENGTH = 20

class NewReceiptPage : MenuPage() {

    private val command = CreateReceiptCommand()

    override fun show(): Action {
        while (true) {
            printToUser("Введите название чека: ", endLine = false)
            val userInput = getUserInput()
            if (userInput.isNotEmpty()) {
                if (userInput.length <= NAME_MAX_LENGTH) {
                    command.name = userInput
                } else {
                    command.name = userInput.substring(0, NAME_MAX_LENGTH)
                }
                return Action.Stub("Show next step page")
            } else {
                printToUser("Название не должно быть пустым")
            }
        }
    }
}
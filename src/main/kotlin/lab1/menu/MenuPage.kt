package lab1.menu

import lab1.service.ConsoleIOService

abstract class MenuPage {

    protected fun getUserInput(): String = ConsoleIOService.getInput()
    protected fun getNotEmptyUserInput(emptyInputMessage: String): String {
        while (true) {
            val userInput = getUserInput()
            if (userInput.isNotEmpty()) return userInput
            printToUser(emptyInputMessage)
        }
    }

    protected fun printToUser(message: String, endLine: Boolean = true) = ConsoleIOService.print(message, endLine)

    protected fun requestNotEmptyInput(message: String, emptyInputMessage: String): String {
        while (true) {
            printToUser(message, endLine = false)
            val userInput = getUserInput()
            if (userInput.isNotEmpty()) {
                return userInput
            }
            printToUser(emptyInputMessage)
        }
    }

    abstract fun show(): Action
}
package lab1.ui.menu

import lab1.ServiceLocator
import lab1.service.ConsoleIOService

abstract class MenuPage {

    private val consoleIOService = ServiceLocator.get(ConsoleIOService::class)

    protected fun getUserInput(): String = consoleIOService.getInput()
    protected fun getNotEmptyUserInput(emptyInputMessage: String): String {
        while (true) {
            val userInput = getUserInput()
            if (userInput.isNotEmpty()) return userInput
            printToUser(emptyInputMessage)
        }
    }

    protected fun printToUser(message: String, endLine: Boolean = true) = consoleIOService.print(message, endLine)

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
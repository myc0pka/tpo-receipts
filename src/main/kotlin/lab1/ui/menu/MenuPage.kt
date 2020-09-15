package lab1.ui.menu

import lab1.ServiceLocator
import lab1.service.ConsoleIOService

abstract class MenuPage {

    private val consoleIOService = ServiceLocator.get(ConsoleIOService::class)

    protected fun getUserInput(): String = consoleIOService.getInput()
    protected fun printToUser(message: String, endLine: Boolean = true) = consoleIOService.print(message, endLine)

    abstract fun show(): Action
}
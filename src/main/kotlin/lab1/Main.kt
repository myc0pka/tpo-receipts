package lab1

import lab1.service.ConsoleIOService
import lab1.ui.menu.Action
import lab1.ui.menu.MainMenuPage

fun main() {
    ServiceLocator.register(ConsoleIOService(), ConsoleIOService::class)

    var action: Action = Action.ShowPage(MainMenuPage())
    while (true) {
        when (action) {
            is Action.Stub -> {
                println("STUB: ${action.message}")
                break
            }
            is Action.ShowPage -> action = action.page.show()
            Action.ExitProgram -> break
        }
    }
}
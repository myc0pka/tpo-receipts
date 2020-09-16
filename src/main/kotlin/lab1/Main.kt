package lab1

import lab1.ui.menu.Action
import lab1.ui.menu.MainMenuPage
import org.jetbrains.exposed.sql.Database

fun main(args: Array<String>) {
    check(args.size >= 2) { "DB username and password are not passed" }
    val dbUser = args[0]
    val dbPassword = args[1]
    Database.connect(
        url = "jdbc:postgresql://balarama.db.elephantsql.com:5432/$dbUser",
        driver = "org.postgresql.Driver",
        user = dbUser,
        password = dbPassword
    )

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
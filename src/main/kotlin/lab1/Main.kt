package lab1

import lab1.db.Consumptions
import lab1.db.Persons
import lab1.db.ReceiptItems
import lab1.db.Receipts
import lab1.menu.Action
import lab1.menu.MainMenuPage
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

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

    transaction {
        addLogger(StdOutSqlLogger)
        SchemaUtils.create(Receipts, Persons, ReceiptItems, Consumptions)
    }

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
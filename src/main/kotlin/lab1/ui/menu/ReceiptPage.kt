package lab1.ui.menu

import lab1.db.ReceiptEntity
import lab1.model.ReceiptItem
import org.jetbrains.exposed.sql.transactions.transaction

class ReceiptPage(private val receiptEntity: ReceiptEntity) :
    OptionsMenuPage<ReceiptPage.Option>(
        options = Option.values().toList(),
        title = "-- Чек '${receiptEntity.name}' --"
    ) {

    enum class Option(override val text: String) : MenuOption {

        PRINT(text = "Распечатать"),
        SHOW_SUMS(text = "Показать суммы"),
        DELETE(text = "Удалить"),
        MAIN_MENU(text = "В главное меню")
    }

    private class PersonWithSum(val personName: String, val sum: Double)

    override fun handleOptionInput(option: Option): Action {
        return when (option) {
            Option.PRINT -> print()
            Option.SHOW_SUMS -> showSums()
            Option.DELETE -> delete()
            Option.MAIN_MENU -> Action.ShowPage(MainMenuPage())
        }
    }

    private fun print(): Action {
        var totalSum = 0.0
        val items = transaction {
            totalSum = receiptEntity.totalSum
            receiptEntity.items.map { ReceiptItem(it.name, it.amount, it.price) }
        }
        items.forEach {
            printToUser("'${it.name}' ${it.amount} шт. * ${it.price}")
        }
        printToUser("ИТОГО: $totalSum")

        return Action.ShowPage(this)
    }

    private fun showSums(): Action {
        var totalSum = 0.0
        var totalConsumedSum = 0.0
        val personWithSumList = transaction {
            totalSum = receiptEntity.totalSum
            receiptEntity.persons.map { person ->
                val consumedSum = person.consumptions.fold(0.0) { acc, c -> acc + c.amount * c.item.price }
                totalConsumedSum += consumedSum
                PersonWithSum(person.name, consumedSum)
            }
        }
        personWithSumList.forEach { printToUser("${it.personName} должен вам ${it.sum}") }
        printToUser("С вас: ${totalSum - totalConsumedSum}")
        printToUser("ИТОГО: $totalSum")

        return Action.ShowPage(this)
    }

    private fun delete(): Action {
        printToUser("Удаление...", endLine = false)
        try {
            transaction { receiptEntity.delete() }
            printToUser("Готово")
        } catch (e: Exception) {
            printToUser("Ошибка. Повторите попытку")
        }
        return Action.ShowPage(MainMenuPage())
    }
}
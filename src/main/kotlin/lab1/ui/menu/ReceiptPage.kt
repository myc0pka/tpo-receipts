package lab1.ui.menu

import lab1.db.ReceiptEntity
import org.jetbrains.exposed.sql.transactions.transaction

class ReceiptPage(private val receiptEntity: ReceiptEntity) :
    OptionsMenuPage<ReceiptPage.Option>(
        options = Option.values().toList(),
        title = "Чек '${receiptEntity.name}'. Действия:"
    ) {

    enum class Option(override val text: String) : MenuOption {

        SHOW_SUMS(text = "Показать суммы"),
        DELETE(text = "Удалить"),
        MAIN_MENU(text = "В главное меню")
    }

    private class PersonWithSum(val personName: String, val sum: Double)

    override fun handleOptionInput(option: Option): Action {
        return when (option) {
            Option.SHOW_SUMS -> {
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

                Action.ShowPage(this)
            }
            Option.DELETE -> {
                printToUser("Удаление...", endLine = false)
                try {
                    transaction { receiptEntity.delete() }
                    printToUser("Готово")
                } catch (e: Exception) {
                    printToUser("Ошибка. Повторите попытку")
                }
                Action.ShowPage(MainMenuPage())
            }
            Option.MAIN_MENU -> Action.ShowPage(MainMenuPage())
        }
    }
}
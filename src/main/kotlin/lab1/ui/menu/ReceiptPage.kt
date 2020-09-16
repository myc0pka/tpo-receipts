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

    override fun handleOptionInput(option: Option): Action {
        return when (option) {
            Option.SHOW_SUMS -> Action.Stub("Show sums")
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
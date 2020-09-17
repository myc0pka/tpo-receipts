package lab1.menu

import lab1.db.ReceiptEntity
import org.jetbrains.exposed.sql.transactions.transaction

class DeletionConfirmationMenuPage(
    private val receiptEntity: ReceiptEntity,
    private val callingPage: ReceiptPage
) : ConfirmationMenuPage(title = "Вы действительно хотите удалить чек '${receiptEntity.name}'?") {

    override fun onYesSelected(): Action {
        printToUser("Удаление...", endLine = false)
        try {
            transaction { receiptEntity.delete() }
            printToUser("Готово")
        } catch (e: Exception) {
            printToUser("Ошибка. Повторите попытку")
        }
        return Action.ShowPage(MainMenuPage())
    }

    override fun onNoSelected(): Action {
        return Action.ShowPage(callingPage)
    }
}
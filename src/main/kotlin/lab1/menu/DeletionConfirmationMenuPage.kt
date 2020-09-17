package lab1.menu

import lab1.model.ReceiptName
import lab1.service.ReceiptRepository

class DeletionConfirmationMenuPage(
    private val namedReceipt: ReceiptName,
    private val callingPage: ReceiptPage
) : ConfirmationMenuPage(title = "Вы действительно хотите удалить чек '${namedReceipt.name}'?") {

    override fun onYesSelected(): Action {
        printToUser("Удаление...", endLine = false)
        try {
            ReceiptRepository.deleteReceipt(namedReceipt.id)
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
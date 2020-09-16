package lab1.ui.menu

import lab1.db.ReceiptEntity
import lab1.db.Receipts
import lab1.service.TokenService
import org.jetbrains.exposed.sql.transactions.transaction

class MainMenuPage : OptionsMenuPage<MainMenuPage.Option>(options = Option.values().toList()) {

    enum class Option(override val text: String) : MenuOption {

        NEW_RECEIPT(text = "Новый чек"),
        HISTORY(text = "Мои чеки"),
        EXIT(text = "Выйти")
    }

    override fun handleOptionInput(option: Option): Action {
        return when (option) {
            Option.NEW_RECEIPT -> Action.ShowPage(NewReceiptPage())
            Option.HISTORY -> {
                val receiptEntities = transaction {
                    ReceiptEntity.find { Receipts.ownerToken eq TokenService.getLocalToken() }.toList()
                }
                if (receiptEntities.isNotEmpty()) {
                    Action.ShowPage(MyReceiptsPage(receiptEntities))
                } else {
                    printToUser("Пока что нет ни одного чека")
                    Action.ShowPage(MainMenuPage())
                }
            }
            Option.EXIT -> Action.ExitProgram
        }
    }
}
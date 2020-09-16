package lab1.ui.menu

import lab1.db.ReceiptEntity
import lab1.db.Receipts
import lab1.service.TokenService
import org.jetbrains.exposed.sql.transactions.transaction

enum class MainMenuOption(override val text: String) : MenuOption {

    NEW_RECEIPT(text = "Новый чек"),
    HISTORY(text = "Мои чеки"),
    EXIT(text = "Выйти")
}

class MainMenuPage : OptionsMenuPage<MainMenuOption>(options = MainMenuOption.values().toList()) {

    override fun handleOptionInput(option: MainMenuOption): Action {
        return when (option) {
            MainMenuOption.NEW_RECEIPT -> Action.ShowPage(NewReceiptPage())
            MainMenuOption.HISTORY -> {
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
            MainMenuOption.EXIT -> Action.ExitProgram
        }
    }
}
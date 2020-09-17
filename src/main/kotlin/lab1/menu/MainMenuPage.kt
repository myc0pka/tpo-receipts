package lab1.menu

import lab1.service.ReceiptRepository
import lab1.service.TokenService

class MainMenuPage : OptionsMenuPage<MainMenuPage.Option>(
    title = "-- Главное меню --",
    options = Option.values().toList()
) {

    enum class Option(override val text: String) : MenuOption {

        NEW_RECEIPT(text = "Новый чек"),
        HISTORY(text = "Мои чеки"),
        EXIT(text = "Выйти")
    }

    override fun handleOptionInput(option: Option): Action {
        return when (option) {
            Option.NEW_RECEIPT -> Action.ShowPage(NewReceiptPage())
            Option.HISTORY -> {
                val namedReceipts = ReceiptRepository.getReceiptNamesByToken(TokenService.getLocalToken())
                if (namedReceipts.isNotEmpty()) {
                    Action.ShowPage(MyReceiptsPage(namedReceipts))
                } else {
                    printToUser("Пока что нет ни одного чека")
                    Action.ShowPage(MainMenuPage())
                }
            }
            Option.EXIT -> Action.ShowPage(ExitConfirmationMenuPage())
        }
    }
}
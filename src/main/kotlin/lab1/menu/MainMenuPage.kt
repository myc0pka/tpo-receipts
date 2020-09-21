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
        AVERAGE_SUM(text = "Показать среднюю сумму"),
        EXIT(text = "Выйти")
    }

    override fun handleOptionInput(option: Option): Action {
        return when (option) {
            Option.NEW_RECEIPT -> newReceipt()
            Option.HISTORY -> showHistory()
            Option.AVERAGE_SUM -> showAverageSum()
            Option.EXIT -> exit()
        }
    }

    private fun newReceipt(): Action {
        return Action.ShowPage(NewReceiptPage())
    }

    private fun showHistory(): Action {
        val namedReceipts = ReceiptRepository.getReceiptNamesByToken(TokenService.getLocalToken())
        return if (namedReceipts.isNotEmpty()) {
            Action.ShowPage(MyReceiptsPage(namedReceipts))
        } else {
            printToUser("Пока что нет ни одного чека")
            Action.ShowPage(MainMenuPage())
        }
    }

    private fun showAverageSum(): Action {
        val averageSum = ReceiptRepository.getAverageSumByToken(TokenService.getLocalToken())
        printToUser("Средняя сумма вашего чека: $averageSum")
        return Action.ShowPage(this)
    }

    private fun exit(): Action {
        return Action.ShowPage(ExitConfirmationMenuPage())
    }
}
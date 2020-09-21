package lab1.menu

import lab1.model.NamedReceipt
import lab1.service.ReceiptRepository

class ReceiptPage(private val namedReceipt: NamedReceipt) :
    OptionsMenuPage<ReceiptPage.Option>(
        options = Option.values().toList(),
        title = "-- Чек '${namedReceipt.name}' --"
    ) {

    enum class Option(override val text: String) : MenuOption {

        PRINT(text = "Распечатать"),
        SHOW_SUMS(text = "Показать суммы"),
        DELETE(text = "Удалить"),
        MAIN_MENU(text = "Вернуться в главное меню")
    }

    override fun handleOptionInput(option: Option): Action {
        return when (option) {
            Option.PRINT -> print()
            Option.SHOW_SUMS -> showSums()
            Option.DELETE -> delete()
            Option.MAIN_MENU -> Action.ShowPage(MainMenuPage())
        }
    }

    private fun print(): Action {
        val items = ReceiptRepository.getReceiptItems(namedReceipt.id)
        val totalSum = ReceiptRepository.getReceiptTotalSum(namedReceipt.id)
        printToUser("- Позиции в чеке '${namedReceipt.name}' -")
        items.forEachIndexed { index, item ->
            printToUser("${index + 1}. ${item.name} ${item.amount} шт. * ${item.price}")
        }
        printToUser("-")
        printToUser("ИТОГО: $totalSum")

        return Action.ShowPage(this)
    }

    private fun showSums(): Action {
        val personTotalConsumptions = ReceiptRepository.getPersonTotalConsumptions(namedReceipt.id)
        val totalSum = ReceiptRepository.getReceiptTotalSum(namedReceipt.id)
        val totalConsumedSum = personTotalConsumptions.fold(0.0) { acc, c -> acc + c.totalConsumption }
        printToUser("- Суммы чека '${namedReceipt.name}' -")
        personTotalConsumptions.forEach { printToUser("${it.personName} должен вам ${it.totalConsumption}") }
        printToUser("С вас: ${totalSum - totalConsumedSum}")
        printToUser("-")
        printToUser("ИТОГО: $totalSum")

        return Action.ShowPage(this)
    }

    private fun delete(): Action {
        return Action.ShowPage(DeletionConfirmationMenuPage(namedReceipt, callingPage = this))
    }
}
package lab1.menu

import lab1.command.CreateReceiptCommand
import lab1.model.ReceiptPreview

class ReceiptPreviewPage(
    private val createReceiptCommand: CreateReceiptCommand
) : OptionsMenuPage<ReceiptPreviewPage.Option>(
    title = createReceiptCommand.preview.headerText,
    options = Option.values().toList()
) {

    companion object {

        private fun StringBuilder.appendItem(itemText: String) {
            append('\t'); append("- "); append(itemText); append('\n')
        }

        private val ReceiptPreview.headerText: String
            get() = buildString {
                append("Чек '"); append(name); append("'\n")
                append("Люди:\n")
                personNames.forEach { appendItem(itemText = it) }
                append("Товары:\n")
                itemSummaries.forEach { appendItem(itemText = it) }
                append("ИТОГО: "); append(totalSum); append('\n')
            }
    }

    enum class Option(override val text: String) : MenuOption {

        CREATE(text = "Создать чек"),
        BACK_TO_ITEMS(text = "Вернуться к добавлению товаров"),
        CANCEL(text = "Вернуться в главное меню")
    }

    override fun handleOptionInput(option: Option): Action {
        return when (option) {
            Option.CREATE -> createReceipt()
            Option.BACK_TO_ITEMS -> backToItems()
            Option.CANCEL -> cancel()
        }
    }

    private fun createReceipt(): Action {
        printToUser("Создание чека...", endLine = false)
        try {
            createReceiptCommand.execute()
            printToUser("Готово")
        } catch (e: Exception) {
            printToUser("Ошибка. Повторите попытку")
        }
        return Action.ShowPage(MainMenuPage())
    }

    private fun backToItems(): Action {
        return Action.ShowPage(ReceiptItemsPage(createReceiptCommand))
    }

    private fun cancel(): Action {
        return Action.ShowPage(MainMenuConfirmationMenuPage(callingPage = this))
    }
}
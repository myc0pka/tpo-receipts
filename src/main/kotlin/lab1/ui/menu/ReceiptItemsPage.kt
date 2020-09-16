package lab1.ui.menu

import lab1.RegularExpressions
import lab1.command.CreateReceiptCommand
import lab1.model.ReceiptItem
import kotlin.math.min

const val RECEIPT_ITEM_NAME_MAX_LENGTH = 20

class ReceiptItemsPage(private val createReceiptCommand: CreateReceiptCommand) :
    OptionsMenuPage<ReceiptItemsPage.Option>(options = Option.values().toList()) {

    enum class Option(override val text: String) : MenuOption {

        ADD_ITEM(text = "Добавить товар"),
        END(text = "Завершить")
    }

    override fun handleOptionInput(option: Option): Action {
        return when (option) {
            Option.ADD_ITEM -> {
                val nameInput = requestNotEmptyInput(
                    message = "Введите название товара: ",
                    emptyInputMessage = "Название не должно быть пустым"
                )
                val amount: Int
                while (true) {
                    val amountInput = requestNotEmptyInput(
                        message = "Введите количество: ",
                        emptyInputMessage = "Количество не должно быть пустым"
                    )
                    if (amountInput.matches(RegularExpressions.POSITIVE_INT)) {
                        amount = amountInput.toInt()
                        break
                    } else {
                        printToUser("Количество должно быть целым положительным числом")
                    }
                }
                val price: Double
                while (true) {
                    val priceInput = requestNotEmptyInput(
                        message = "Введите цену: ",
                        emptyInputMessage = "Цена не должна быть пустой"
                    )
                    if (priceInput.matches(RegularExpressions.POSITIVE_REAL)) {
                        price = priceInput.toDouble()
                        break
                    } else {
                        printToUser("Цена должна быть целым вещественным числом")
                    }
                }

                val item = ReceiptItem(
                    name = nameInput.substring(0, min(nameInput.length, RECEIPT_ITEM_NAME_MAX_LENGTH)),
                    amount, price
                )
                createReceiptCommand.addItem(item)
                Action.ShowPage(
                    ReceiptConsumptionPage(
                        createReceiptCommand,
                        consumedItem = item,
                        availableAmount = item.amount
                    )
                )
            }
            Option.END -> {
                if (createReceiptCommand.itemCount == 0) {
                    printToUser("Добавьте по крайней мере один товар")
                    Action.ShowPage(this)
                } else {
                    printToUser("Создание чека...", endLine = false)
                    if (createReceiptCommand.execute() == CreateReceiptCommand.Result.Ok) {
                        printToUser("Готово")
                    }
                    Action.ShowPage(MainMenuPage())
                }
            }
        }
    }
}
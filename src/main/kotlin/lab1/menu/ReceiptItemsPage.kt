package lab1.menu

import lab1.command.CreateReceiptCommand
import lab1.model.ReceiptItem
import kotlin.math.min

const val RECEIPT_ITEM_NAME_MAX_LENGTH = 20

private const val MIN_AMOUNT = 1
private const val MAX_AMOUNT = 100
private const val INVALID_AMOUNT_ERROR_MESSAGE = "Количество должно быть целым положительным числом от 1 до 100"

private const val MAX_PRICE = 1_000_000_000.0
private const val INVALID_PRICE_ERROR_MESSAGE = "Цена должна быть целым вещественным числом больше 0 до 1 000 000 000"

class ReceiptItemsPage(private val createReceiptCommand: CreateReceiptCommand) :
    OptionsMenuPage<ReceiptItemsPage.Option>(options = Option.values().toList()) {

    enum class Option(override val text: String) : MenuOption {

        ADD_ITEM(text = "Добавить товар"),
        BACK_TO_PEOPLE(text = "Вернуться к добавлению людей"),
        END(text = "Завершить создание чека"),
        CANCEL(text = "Вернуться в главное меню")
    }

    override fun handleOptionInput(option: Option): Action {
        return when (option) {
            Option.ADD_ITEM -> addItem()
            Option.END -> end()
            Option.BACK_TO_PEOPLE -> backToPeople()
            Option.CANCEL -> cancel()
        }
    }

    private fun addItem(): Action {
        val name: String
        while (true) {
            val nameInput = requestNotEmptyInput(
                message = "Введите название товара: ",
                emptyInputMessage = "Название не должно быть пустым"
            )
            if (!createReceiptCommand.hasItemWithName(nameInput)) {
                name = nameInput
                break
            } else {
                printToUser("Ошибка: товар с именем '$nameInput' был добавлен ранее")
            }
        }
        var amount: Int
        while (true) {
            val amountInput = requestNotEmptyInput(
                message = "Введите количество: ",
                emptyInputMessage = "Количество не должно быть пустым"
            )
            try {
                val uncheckedAmount = amountInput.toInt()
                if (uncheckedAmount in MIN_AMOUNT..MAX_AMOUNT) {
                    amount = uncheckedAmount
                    break
                } else {
                    printToUser(INVALID_AMOUNT_ERROR_MESSAGE)
                }
            } catch (e: NumberFormatException) {
                printToUser(INVALID_AMOUNT_ERROR_MESSAGE)
            }
        }
        var price: Double
        while (true) {
            val priceInput = requestNotEmptyInput(
                message = "Введите цену: ",
                emptyInputMessage = "Цена не должна быть пустой"
            )
            try {
                val uncheckedPrice = priceInput.toDouble()
                if (uncheckedPrice > 0 && uncheckedPrice <= MAX_PRICE) {
                    price = uncheckedPrice
                    break
                } else {
                    printToUser(INVALID_PRICE_ERROR_MESSAGE)
                }
            } catch (e: NumberFormatException) {
                printToUser(INVALID_PRICE_ERROR_MESSAGE)
            }
        }

        val item = ReceiptItem(
            name = name.substring(0, min(name.length, RECEIPT_ITEM_NAME_MAX_LENGTH)),
            amount, price
        )
        createReceiptCommand.addItem(item)
        return Action.ShowPage(
            ReceiptConsumptionPage(
                createReceiptCommand,
                consumedItem = item,
                availableAmount = item.amount
            )
        )
    }

    private fun backToPeople(): Action {
        return Action.ShowPage(ReceiptPeoplePage(createReceiptCommand))
    }

    private fun end(): Action {
        return if (createReceiptCommand.itemCount == 0) {
            printToUser("Добавьте по крайней мере один товар")
            Action.ShowPage(this)
        } else {
            printToUser("Создание чека...", endLine = false)
            try {
                createReceiptCommand.execute()
                printToUser("Готово")
            } catch (e: Exception) {
                printToUser("Ошибка. Повторите попытку")
                e.printStackTrace()
            }
            Action.ShowPage(MainMenuPage())
        }
    }

    private fun cancel(): Action {
        return Action.ShowPage(MainMenuConfirmationMenuPage(callingPage = this))
    }
}
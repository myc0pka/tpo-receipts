package lab1.menu

import lab1.command.CreateReceiptCommand
import lab1.model.Consumption
import lab1.model.Person
import lab1.model.ReceiptItem

class ReceiptConsumptionPage(
    private val createReceiptCommand: CreateReceiptCommand,
    private val consumedItem: ReceiptItem,
    private val availableAmount: Int
) : OptionsMenuPage<ReceiptConsumptionPage.Option>(
    title = "Кто покупал '${consumedItem.name}'? (осталось $availableAmount)",
    options = createReceiptCommand.persons.map { Option.AddConsumption(it) } + Option.RestIsMine + Option.Cancel
) {

    sealed class Option(override val text: String) : MenuOption {

        class AddConsumption(val consumer: Person) : Option(text = consumer.name)
        object RestIsMine : Option(text = "Остальное брал я")
        object Cancel : Option(text = "Вернуться в главное меню")
    }

    override fun handleOptionInput(option: Option): Action {
        return when (option) {
            is Option.AddConsumption -> addConsumption(option.consumer)
            Option.RestIsMine -> end()
            Option.Cancel -> cancel()
        }
    }

    private fun printErrorMessage() = printToUser("Введите число от 0 до $availableAmount")

    private fun addConsumption(consumer: Person): Action {
        while (true) {
            val consumedAmountInput = requestNotEmptyInput(
                message = "Введите количество: ",
                emptyInputMessage = "Количество не должно быть пустым"
            )
            try {
                val consumedAmount = consumedAmountInput.toInt()
                if (consumedAmount in 1..availableAmount) {
                    val consumption = Consumption(
                        item = consumedItem,
                        person = consumer,
                        amount = consumedAmount
                    )
                    createReceiptCommand.addConsumption(consumption)
                    return if (consumedAmount == availableAmount) {
                        Action.ShowPage(ReceiptItemsPage(createReceiptCommand))
                    } else {
                        Action.ShowPage(
                            ReceiptConsumptionPage(
                                createReceiptCommand,
                                consumedItem,
                                availableAmount = availableAmount - consumedAmount
                            )
                        )
                    }
                } else {
                    printErrorMessage()
                }
            } catch (e: NumberFormatException) {
                printErrorMessage()
            }
        }
    }

    private fun end(): Action {
        return Action.ShowPage(ReceiptItemsPage(createReceiptCommand))
    }

    private fun cancel(): Action {
        return Action.ShowPage(MainMenuConfirmationMenuPage(callingPage = this))
    }
}
package lab1.ui.menu

import lab1.RegularExpressions
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
    options = createReceiptCommand.persons.map { Option.AddConsumption(it) } + Option.End
) {

    sealed class Option(override val text: String) : MenuOption {

        object End : Option(text = "Готово")
        class AddConsumption(val person: Person) : Option(text = person.name)
    }

    override fun handleOptionInput(option: Option): Action {
        when (option) {
            is Option.AddConsumption -> {
                while (true) {
                    val consumedAmountInput = requestNotEmptyInput(
                        message = "Введите количество: ",
                        emptyInputMessage = "Количество не должно быть пустым"
                    )
                    if (consumedAmountInput.matches(RegularExpressions.POSITIVE_INT)) {
                        val consumedAmount = consumedAmountInput.toInt()
                        if (consumedAmount <= availableAmount) {
                            val consumption = Consumption(
                                item = consumedItem,
                                person = option.person,
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
                            printToUser("Количество должно быть не больше остатка ($availableAmount)")
                        }
                    } else {
                        printToUser("Количество должно быть целым положительным числом")
                    }
                }
            }
            Option.End -> return Action.ShowPage(ReceiptItemsPage(createReceiptCommand))
        }
    }
}
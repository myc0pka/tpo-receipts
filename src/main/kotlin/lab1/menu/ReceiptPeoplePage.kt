package lab1.menu

import lab1.command.CreateReceiptCommand
import lab1.model.Person
import java.lang.Integer.min

const val PERSON_NAME_MAX_LENGTH = 50

class ReceiptPeoplePage(private val createReceiptCommand: CreateReceiptCommand) :
    OptionsMenuPage<ReceiptPeoplePage.Option>(options = Option.values().toList()) {

    enum class Option(override val text: String) : MenuOption {

        ADD_PERSON(text = "Добавить человека"),
        END(text = "Перейти к добавлению товаров"),
        CANCEL(text = "Вернуться в главное меню")
    }

    override fun handleOptionInput(option: Option): Action {
        return when (option) {
            Option.ADD_PERSON -> addPerson()
            Option.END -> end()
            Option.CANCEL -> cancel()
        }
    }

    private fun addPerson(): Action {
        while (true) {
            val personNameInput = requestNotEmptyInput(
                message = "Введите имя человека: ",
                emptyInputMessage = "Имя не должно быть пустым"
            )
            val personName = personNameInput.substring(0, min(personNameInput.length, PERSON_NAME_MAX_LENGTH))
            if (!createReceiptCommand.hasPersonWithName(personName)) {
                createReceiptCommand.addPerson(Person(personName))
                break
            } else {
                printToUser("Ошибка: человек с именем '$personName' был добавлен ранее")
            }
        }

        return Action.ShowPage(this)
    }

    private fun end(): Action {
        return if (createReceiptCommand.personCount == 0) {
            printToUser("Добавьте по крайней мере одного человека")
            Action.ShowPage(this)
        } else {
            Action.ShowPage(ReceiptItemsPage(createReceiptCommand))
        }
    }

    private fun cancel(): Action {
        return Action.ShowPage(MainMenuConfirmationMenuPage(callingPage = this))
    }
}
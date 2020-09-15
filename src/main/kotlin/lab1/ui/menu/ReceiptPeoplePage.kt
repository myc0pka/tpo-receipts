package lab1.ui.menu

import lab1.command.CreateReceiptCommand
import lab1.model.Person
import java.lang.Integer.min

enum class ReceiptPeopleOption(override val text: String) : MenuOption {

    ADD_PERSON(text = "Добавить человека"),
    END(text = "Перейти к следующему шагу")
}

private const val MAX_NAME_LENGTH = 50

class ReceiptPeoplePage(private val createReceiptCommand: CreateReceiptCommand) :
    OptionsMenuPage<ReceiptPeopleOption>(ReceiptPeopleOption.values().toList()) {

    override fun handleOptionInput(option: ReceiptPeopleOption): Action {
        return when (option) {
            ReceiptPeopleOption.ADD_PERSON -> {
                val personNameInput = requestNotEmptyInput(
                    message = "Введите имя человека: ",
                    emptyInputMessage = "Имя не должно быть пустым"
                )
                val person = Person(name = personNameInput.substring(0, min(personNameInput.length, MAX_NAME_LENGTH)))
                createReceiptCommand.addPerson(person)

                Action.ShowPage(this)
            }
            ReceiptPeopleOption.END -> {
                if (createReceiptCommand.personCount == 0) {
                    printToUser("Добавьте по крайней мере одного человека")
                    Action.ShowPage(this)
                } else {
                    Action.Stub("New step")
                }
            }
        }
    }
}
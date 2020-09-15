package lab1.ui.menu

interface MenuOption {

    val text: String
}

abstract class OptionsMenuPage<Option : MenuOption>(
    private val title: String? = null,
    private val options: List<Option>
) : MenuPage() {

    companion object {

        private val POSITIVE_INT_REGEX = Regex("[1-9]\\d*")
    }

    override fun show(): Action {
        if (title != null) {
            printToUser(title)
        }

        val optionsMessage = buildString {
            options.forEachIndexed { index, option -> append("${index + 1}) ${option.text}\n") }
        }
        printToUser(optionsMessage)

        while (true) {
            printToUser("Выберите опцию (1-${options.size}): ", endLine = false)
            val userInput = getUserInput()
            if (userInput.matches(POSITIVE_INT_REGEX)) {
                val optionNumber = userInput.toInt()
                val optionIndex = optionNumber - 1
                if (optionIndex in options.indices) {
                    return handleOptionInput(options[optionIndex])
                } else {
                    printToUser("Некорректный номер : $optionNumber")
                }
            } else {
                printToUser("Некорректный ввод : '$userInput'")
            }
        }
    }

    abstract fun handleOptionInput(option: Option): Action
}
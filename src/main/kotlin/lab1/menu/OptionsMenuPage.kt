package lab1.menu

interface MenuOption {

    val text: String
}

abstract class OptionsMenuPage<Option : MenuOption>(
    private val title: String? = null,
    private val options: List<Option>
) : MenuPage() {

    override fun show(): Action {
        if (title != null) {
            printToUser(title)
        }

        options.forEachIndexed { index, option -> printToUser("${index + 1}) ${option.text}") }

        while (true) {
            printToUser("Выберите опцию (1-${options.size}): ", endLine = false)
            val userInput = getUserInput()
            try {
                val optionNumber = userInput.toInt()
                val optionIndex = optionNumber - 1
                if (optionIndex in options.indices) {
                    return handleOptionInput(options[optionIndex])
                } else {
                    printToUser("Некорректный номер : $optionNumber")
                }
            } catch (e: NumberFormatException) {
                printToUser("Некорректный ввод : '$userInput'")
            }
        }
    }

    abstract fun handleOptionInput(option: Option): Action
}
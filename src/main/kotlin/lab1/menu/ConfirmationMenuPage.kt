package lab1.menu

abstract class ConfirmationMenuPage(private val title: String) :
    OptionsMenuPage<ConfirmationMenuPage.Option>(title, options = Option.values().toList()) {

    enum class Option(override val text: String) : MenuOption {

        YES(text = "Да"),
        NO(text = "Нет")
    }

    override fun handleOptionInput(option: Option): Action {
        return when (option) {
            Option.YES -> onYesSelected()
            Option.NO -> onNoSelected()
        }
    }

    abstract fun onYesSelected(): Action
    abstract fun onNoSelected(): Action
}
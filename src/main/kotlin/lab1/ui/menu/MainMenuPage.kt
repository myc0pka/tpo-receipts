package lab1.ui.menu

enum class MainMenuOption(override val text: String) : MenuOption {

    NEW_RECEIPT(text = "Новый чек"),
    HISTORY(text = "Показать историю"),
    EXIT(text = "Выйти")
}

class MainMenuPage : OptionsMenuPage<MainMenuOption>(options = MainMenuOption.values().toList()) {

    override fun handleOptionInput(option: MainMenuOption): Action {
        return when (option) {
            MainMenuOption.NEW_RECEIPT -> Action.Stub("Show new receipt page")
            MainMenuOption.HISTORY -> Action.Stub("Show history page")
            MainMenuOption.EXIT -> Action.ExitProgram
        }
    }
}
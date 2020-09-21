package lab1.menu

import lab1.model.NamedReceipt

class MyReceiptsPage(namedReceipts: List<NamedReceipt>) : OptionsMenuPage<MyReceiptsPage.Option>(
    title = "-- Мои чеки --",
    options = namedReceipts.map { Option.Item(it) } + Option.MainMenu
) {

    sealed class Option(override val text: String) : MenuOption {

        class Item(val namedReceipt: NamedReceipt) : Option(text = namedReceipt.name)
        object MainMenu : Option(text = "Вернуться в главное меню")
    }

    override fun handleOptionInput(option: Option): Action {
        return when (option) {
            is Option.Item -> Action.ShowPage(ReceiptPage(option.namedReceipt))
            Option.MainMenu -> Action.ShowPage(MainMenuPage())
        }
    }
}
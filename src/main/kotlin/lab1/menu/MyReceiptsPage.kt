package lab1.menu

import lab1.db.ReceiptEntity

class MyReceiptsPage(
    private val receiptEntities: List<ReceiptEntity>
) : OptionsMenuPage<MyReceiptsPage.Option>(
    title = "-- Мои чеки --",
    options = receiptEntities.map { Option.Item(it) } + Option.MainMenu
) {

    sealed class Option(override val text: String) : MenuOption {

        class Item(val receiptEntity: ReceiptEntity) : Option(text = receiptEntity.name)
        object MainMenu : Option(text = "Вернуться в главное меню")
    }

    override fun handleOptionInput(option: Option): Action {
        return when (option) {
            is Option.Item -> Action.ShowPage(ReceiptPage(option.receiptEntity))
            Option.MainMenu -> Action.ShowPage(MainMenuPage())
        }
    }
}
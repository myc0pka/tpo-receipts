package lab1.menu

class MainMenuConfirmationMenuPage(
    private val callingPage: MenuPage
) : ConfirmationMenuPage(title = "Вы действительно хотите вернуться в главное меню?") {

    override fun onYesSelected(): Action {
        return Action.ShowPage(MainMenuPage())
    }

    override fun onNoSelected(): Action {
        return Action.ShowPage(callingPage)
    }
}
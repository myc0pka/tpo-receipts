package lab1.menu

class ExitConfirmationMenuPage : ConfirmationMenuPage(title = "Вы действительно хотите выйти?") {

    override fun onYesSelected(): Action {
        return Action.ExitProgram
    }

    override fun onNoSelected(): Action {
        return Action.ShowPage(MainMenuPage())
    }
}
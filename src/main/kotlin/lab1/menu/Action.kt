package lab1.menu

sealed class Action {

    class Stub(val message: String) : Action()
    class ShowPage(val page: MenuPage) : Action()
    object ExitProgram : Action()
}
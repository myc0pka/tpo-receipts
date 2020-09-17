package lab1.menu

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import lab1.command.CreateReceiptCommand
import lab1.model.Person
import lab1.service.ConsoleIOService
import org.junit.jupiter.api.*

class ReceiptPeoplePageTest : BaseMenuPageTest() {

    private val createCommandMock = mockk<CreateReceiptCommand>(relaxed = true) {
        every { hasPersonWithName(any()) } returns false
    }
    private val receiptPeoplePage = ReceiptPeoplePage(createCommandMock)

    @Test
    @DisplayName("On show should print menu options")
    fun onShow() {
        every { ConsoleIOService.getInput() } returns "1"
        receiptPeoplePage.show()
        verify { ConsoleIOService.print("1) Добавить человека", endLine = true) }
        verify { ConsoleIOService.print("2) Перейти к добавлению товаров", endLine = true) }
        verify { ConsoleIOService.print("3) Вернуться в главное меню", endLine = true) }
    }

    @Nested
    @DisplayName("When add person option selected")
    inner class AddPersonSelected {

        private val getInputMocking by lazy {
            every { ConsoleIOService.getInput() } returns "1"
        }

        @Test
        @DisplayName("When empty name input show() should print error message and request input again")
        fun emptyNameInput() {
            getInputMocking.andThen("").andThen("a")

            receiptPeoplePage.show()
            verify { ConsoleIOService.print("Имя не должно быть пустым", endLine = true) }
            verify(exactly = 0) { createCommandMock.addPerson(Person("")) }
        }

        @Test
        @DisplayName("When too long name input show() should cut it and return ShowPage(this)")
        fun tooLongNameInput() {
            val tooLongName = buildString {
                repeat(51) { append('a') }
            }
            val cutName = buildString {
                repeat(50) { append('a') }
            }
            getInputMocking.andThen(tooLongName)

            Assertions.assertEquals(receiptPeoplePage, (receiptPeoplePage.show() as Action.ShowPage).page)
            verify { createCommandMock.addPerson(Person(cutName)) }
        }

        @Test
        @DisplayName("When repeated name input show() should print error message and request input again")
        fun repeatedNameInput() {
            val repeatedName = "Repeated Name"
            getInputMocking.andThen(repeatedName).andThen("a")
            every { createCommandMock.hasPersonWithName(repeatedName) } returns true

            receiptPeoplePage.show()
            verify {
                ConsoleIOService.print(
                    message = "Ошибка: человек с именем '$repeatedName' был добавлен ранее",
                    endLine = true
                )
            }
            verify(exactly = 0) { createCommandMock.addPerson(Person(repeatedName)) }
        }

        @Test
        @DisplayName("When valid name input show() should return ShowPage(this)")
        fun validName() {
            val validName = "Valid Name"
            getInputMocking.andThen(validName)

            Assertions.assertEquals(receiptPeoplePage, (receiptPeoplePage.show() as Action.ShowPage).page)
            verify { createCommandMock.addPerson(Person(validName)) }
        }
    }

    @Nested
    @DisplayName("When go to items options selected")
    inner class GoToItemsSelected {

        @BeforeEach
        fun setUp() {
            every { ConsoleIOService.getInput() } returns "2"
        }

        @Test
        @DisplayName("When no people added show() should print error message and return ShowPage(this)")
        fun noPeopleAdded() {
            every { createCommandMock getProperty "personCount" } returns 0

            Assertions.assertEquals(receiptPeoplePage, (receiptPeoplePage.show() as Action.ShowPage).page)
            verify { ConsoleIOService.print("Добавьте по крайней мере одного человека", endLine = true) }
        }

        @Test
        @DisplayName("When one person added show() should return ShowPage(ReceiptItemsPage)")
        fun onePersonAdded() {
            every { createCommandMock getProperty "personCount" } returns 1

            Assertions.assertEquals(
                ReceiptItemsPage::class,
                (receiptPeoplePage.show() as Action.ShowPage).page::class
            )
        }
    }

    @Test
    @DisplayName("When cancel option selected show() should return ShowPage(MainMenuConfirmationPage)")
    fun cancelSelected() {
        every { ConsoleIOService.getInput() } returns "3"

        Assertions.assertEquals(
            MainMenuConfirmationMenuPage::class,
            (receiptPeoplePage.show() as Action.ShowPage).page::class
        )
    }
}
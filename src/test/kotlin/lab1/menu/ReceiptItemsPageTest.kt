package lab1.menu

import io.mockk.MockKAdditionalAnswerScope
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import lab1.command.CreateReceiptCommand
import lab1.model.ReceiptItem
import lab1.service.ConsoleIOService
import org.junit.jupiter.api.*

class ReceiptItemsPageTest : BaseMenuPageTest() {

    private val createCommandMock = mockk<CreateReceiptCommand>(relaxed = true) {
        every { hasItemWithName(any()) } returns false
    }
    private val receiptItemsPage = ReceiptItemsPage(createCommandMock)

    @Test
    @DisplayName("On show should print menu options")
    fun onShow() {
        every { ConsoleIOService.getInput() } returns "1"

        receiptItemsPage.show()
        verify { ConsoleIOService.print("1) Добавить товар", endLine = true) }
        verify { ConsoleIOService.print("2) Завершить создание чека", endLine = true) }
        verify { ConsoleIOService.print("3) Вернуться в главное меню", endLine = true) }
    }

    @Nested
    @DisplayName("When add item option selected")
    inner class AddItemSelected {

        val mockedOptionNumberInput by lazy {
            every { ConsoleIOService.getInput() } returns "1"
        }

        private fun MockKAdditionalAnswerScope<String, String>.andThenValidNameAmountAndPrice() {
            andThen("a").andThen("1").andThen("1.0")
        }

        @Test
        @DisplayName("When empty name input show() should print error message and request input again")
        fun emptyNameInput() {
            mockedOptionNumberInput.andThen("").andThenValidNameAmountAndPrice()

            receiptItemsPage.show()
            verify { ConsoleIOService.print("Название не должно быть пустым", endLine = true) }
        }

        @Test
        @DisplayName("When repeated name input show() should print error message and request input again")
        fun repeatedNameInput() {
            val repeatedName = "Repeated Name"
            mockedOptionNumberInput.andThen(repeatedName).andThenValidNameAmountAndPrice()
            every { createCommandMock.hasItemWithName(repeatedName) } returns true

            receiptItemsPage.show()
            verify {
                ConsoleIOService.print(
                    message = "Ошибка: товар с именем '$repeatedName' был добавлен ранее",
                    endLine = true
                )
            }
        }

        @Nested
        @DisplayName("When input name accepted")
        inner class NameAccepted {

            val mockedNameInput by lazy { mockedOptionNumberInput.andThen("Item Name") }

            private fun MockKAdditionalAnswerScope<String, String>.andThenValidAmountAndPrice() {
                andThen("1").andThen("1.0")
            }

            @Test
            @DisplayName("When empty amount input show() should print error message and request input again")
            fun emptyAmountInput() {
                mockedNameInput.andThen("").andThenValidAmountAndPrice()

                receiptItemsPage.show()
                verify { ConsoleIOService.print("Количество не должно быть пустым", endLine = true) }
            }

            @Test
            @DisplayName("When non-integer amount input show() should print error message and request input again")
            fun nonIntAmountInput() {
                mockedNameInput.andThen("adf").andThenValidAmountAndPrice()

                receiptItemsPage.show()
                verify {
                    ConsoleIOService.print(
                        message = "Количество должно быть целым положительным числом от 1 до 100",
                        endLine = true
                    )
                }
            }

            @Test
            @DisplayName("When input amount > 100 show() should print error message and request input again")
            fun tooBigAmountInput() {
                mockedNameInput.andThen("101").andThenValidAmountAndPrice()

                receiptItemsPage.show()
                verify {
                    ConsoleIOService.print(
                        message = "Количество должно быть целым положительным числом от 1 до 100",
                        endLine = true
                    )
                }
            }

            @Test
            @DisplayName("When input amount = 100 show() should print error message and request input again")
            fun zeroAmountInput() {
                mockedNameInput.andThen("0").andThenValidAmountAndPrice()

                receiptItemsPage.show()
                verify {
                    ConsoleIOService.print(
                        message = "Количество должно быть целым положительным числом от 1 до 100",
                        endLine = true
                    )
                }
            }

            @Nested
            @DisplayName("When input amount accepted")
            inner class AmountAccepted {

                private val mockedAmountInput by lazy { mockedNameInput.andThen("100") }

                private fun MockKAdditionalAnswerScope<String, String>.andThenValidPrice() {
                    andThen("1.0")
                }

                @Test
                @DisplayName("When empty price input show() should print error message and request input again")
                fun emptyPriceInput() {
                    mockedAmountInput.andThen("").andThenValidPrice()

                    receiptItemsPage.show()
                    verify { ConsoleIOService.print("Цена не должна быть пустой", endLine = true) }
                }

                @Test
                @DisplayName("When not positive real number price input show() should print error message and request input again")
                fun notPositiveRealPriceInput() {
                    mockedAmountInput.andThen("daf").andThenValidPrice()

                    receiptItemsPage.show()
                    verify {
                        ConsoleIOService.print(
                            message = "Цена должна быть целым вещественным числом больше 0 до 1 000 000 000",
                            endLine = true
                        )
                    }
                }

                @Test
                @DisplayName("When zero price input show() should print error message and request input again")
                fun zeroPriceInput() {
                    mockedAmountInput.andThen("0").andThenValidPrice()

                    receiptItemsPage.show()
                    verify {
                        ConsoleIOService.print(
                            message = "Цена должна быть целым вещественным числом больше 0 до 1 000 000 000",
                            endLine = true
                        )
                    }
                }

                @Test
                @DisplayName("When input price > 1 000 000 000 show() should print error message and request input again")
                fun tooBigPriceInput() {
                    mockedAmountInput.andThen("1000000001").andThenValidPrice()

                    receiptItemsPage.show()
                    verify {
                        ConsoleIOService.print(
                            message = "Цена должна быть целым вещественным числом больше 0 до 1 000 000 000",
                            endLine = true
                        )
                    }
                }

                @Test
                @DisplayName("When valid price input show() should create item and return ShowPage(ReceiptConsumptionPage)")
                fun validPriceInput() {
                    mockedAmountInput.andThen("0.01")

                    Assertions.assertEquals(
                        ReceiptConsumptionPage::class,
                        (receiptItemsPage.show() as Action.ShowPage).page::class
                    )
                    verify { createCommandMock.addItem(ReceiptItem("Item Name", amount = 100, price = 0.01)) }
                }
            }
        }
    }

    @Nested
    @DisplayName("When end option selected")
    inner class EndSelected {

        @BeforeEach
        fun setUp() {
            every { ConsoleIOService.getInput() } returns "2"
        }

        @Test
        @DisplayName("When no items have been added show() should print error message and return ShowPage(this)")
        fun noItemsAdded() {
            every { createCommandMock getProperty "itemCount" } returns 0

            Assertions.assertEquals(receiptItemsPage, (receiptItemsPage.show() as Action.ShowPage).page)
            verify { ConsoleIOService.print("Добавьте по крайней мере один товар", endLine = true) }
        }

        @Nested
        @DisplayName("When one item has been added")
        inner class OneItemAdded {

            @BeforeEach
            fun setUp() {
                every { createCommandMock getProperty "itemCount" } returns 1
            }

            @Test
            @DisplayName("show() should print info message and call CreateReceiptCommand#execute()")
            fun anyway() {
                receiptItemsPage.show()

                verify { ConsoleIOService.print("Создание чека...", endLine = false) }
                verify { createCommandMock.execute() }
            }

            @Test
            @DisplayName("When create receipt operation fails show() should print error message and return ShowPage(MainMenuPage)")
            fun createReceiptOperationFails() {
                every { createCommandMock.execute() } throws Exception()

                Assertions.assertEquals(
                    MainMenuPage::class,
                    (receiptItemsPage.show() as Action.ShowPage).page::class
                )
                verify { ConsoleIOService.print("Ошибка. Повторите попытку", endLine = true) }
            }

            @Test
            @DisplayName("When create receipt operation succeeds show() should print success message and return ShowPage(MainMenuPage)")
            fun createReceiptOperationSucceeds() {
                every { createCommandMock.execute() } returns Unit

                Assertions.assertEquals(
                    MainMenuPage::class,
                    (receiptItemsPage.show() as Action.ShowPage).page::class
                )
                verify { ConsoleIOService.print("Готово", endLine = true) }
            }
        }
    }

    @Test
    @DisplayName("When go to main menu option selected show() should return ShowPage(MainMenuConfirmationPage)")
    fun goToMainMenuSelected() {
        every { ConsoleIOService.getInput() } returns "3"

        Assertions.assertEquals(
            MainMenuConfirmationMenuPage::class,
            (receiptItemsPage.show() as Action.ShowPage).page::class
        )
    }
}
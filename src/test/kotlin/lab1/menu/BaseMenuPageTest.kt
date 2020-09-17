package lab1.menu

import io.mockk.mockkObject
import lab1.service.ConsoleIOService
import lab1.service.ReceiptRepository
import lab1.service.TokenService
import org.junit.jupiter.api.BeforeEach

abstract class BaseMenuPageTest {

    @BeforeEach
    fun mockObjects() {
        mockkObject(ConsoleIOService)
        mockkObject(TokenService)
        mockkObject(ReceiptRepository)
    }
}
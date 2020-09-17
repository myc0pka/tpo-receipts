package lab1.menu

import io.mockk.every
import io.mockk.mockkObject
import lab1.service.ConsoleIOService
import lab1.service.ReceiptRepository
import lab1.service.TokenService
import org.junit.jupiter.api.BeforeEach

abstract class BaseMenuPageTest {

    companion object {

        const val LOCAL_TOKEN = "f394qgrhd"
    }

    @BeforeEach
    fun mockObjects() {
        mockkObject(ConsoleIOService)
        mockkObject(TokenService)
        every { TokenService.getLocalToken() } returns LOCAL_TOKEN
        mockkObject(ReceiptRepository)
    }
}
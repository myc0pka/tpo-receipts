package lab1.service

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.random.Random

private const val LOCAL_TOKEN_FILE_NAME = ".token"
private const val TOKEN_SIZE = 30
const val ENCODED_TOKEN_SIZE = TOKEN_SIZE * 4 / 3

object TokenService {

    private var localToken: String? = null

    private fun createLocalToken(): String {
        val tokenBytes = Random.nextBytes(size = TOKEN_SIZE)
        val encodedToken = Base64.getEncoder().encode(tokenBytes)
        FileOutputStream(File(LOCAL_TOKEN_FILE_NAME)).use { it.write(encodedToken) }
        return String(encodedToken, StandardCharsets.ISO_8859_1).also { localToken = it }
    }

    fun getLocalToken(): String {
        if (localToken != null) return localToken!!

        val tokenFile = File(LOCAL_TOKEN_FILE_NAME)
        return if (tokenFile.exists()) {
            val tokenBytes = ByteArray(ENCODED_TOKEN_SIZE)
            FileInputStream(tokenFile).use { it.read(tokenBytes) }
            String(tokenBytes, StandardCharsets.ISO_8859_1)
        } else {
            createLocalToken()
        }
    }
}
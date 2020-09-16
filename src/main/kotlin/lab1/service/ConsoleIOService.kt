package lab1.service

import java.util.*

object ConsoleIOService {

    private val scanner = Scanner(System.`in`)

    fun getInput(): String {
        return scanner.nextLine()
    }

    fun print(message: String, endLine: Boolean) {
        if (endLine) {
            println(message)
        } else {
            print(message)
        }
    }
}
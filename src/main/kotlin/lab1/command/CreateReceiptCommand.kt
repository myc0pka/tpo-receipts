package lab1.command

import lab1.db.ConsumptionEntity
import lab1.db.PersonEntity
import lab1.db.ReceiptEntity
import lab1.db.ReceiptItemEntity
import lab1.model.Consumption
import lab1.model.Person
import lab1.model.ReceiptItem
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

class CreateReceiptCommand(private val ownerToken: String) {

    sealed class Result {

        object Ok : Result()
        object Failure : Result()
    }

    var name: String? = null
    var persons: List<Person> = emptyList()
        private set
    private var items: List<ReceiptItem> = emptyList()
    private var consumptions: List<Consumption> = emptyList()

    val personCount: Int
        get() = persons.size
    val itemCount: Int
        get() = items.size

    fun addPerson(person: Person) {
        persons = persons + person
    }

    fun addItem(item: ReceiptItem) {
        items = items + item
    }

    fun addConsumption(consumption: Consumption) {
        consumptions = consumptions + consumption
    }

    fun execute(): Result {
        val receiptTotalSum = items.fold(0.0) { acc, item -> acc + item.amount * item.price }
        return try {
            transaction {
                val receiptEntity = ReceiptEntity.new {
                    ownerToken = this@CreateReceiptCommand.ownerToken
                    name = this@CreateReceiptCommand.name!!
                    totalSum = receiptTotalSum
                }
                val personEntities = persons.map {
                    PersonEntity.new {
                        receipt = receiptEntity
                        name = it.name
                    }
                }
                val receiptItemEntities = items.map {
                    ReceiptItemEntity.new {
                        receipt = receiptEntity
                        name = it.name
                        amount = it.amount
                        price = it.price
                    }
                }
                consumptions.forEach { consumption ->
                    ConsumptionEntity.new {
                        person = personEntities.find { it.name == consumption.person.name }!!
                        item = receiptItemEntities.find { it.name == consumption.item.name }!!
                        amount = consumption.amount
                    }
                }
            }
            Result.Ok
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Failure
        }
    }
}
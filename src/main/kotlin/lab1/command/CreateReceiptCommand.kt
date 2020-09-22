package lab1.command

import lab1.model.*
import lab1.service.ReceiptRepository

class CreateReceiptCommand(private val ownerToken: String) {

    var name: String? = null
    var persons: List<Person> = emptyList()
        private set
    private var items: List<ReceiptItem> = emptyList()
    private var consumptions: List<Consumption> = emptyList()

    val personCount: Int
        get() = persons.size
    val itemCount: Int
        get() = items.size

    private val ReceiptItem.summary: String
        get() = "'$name' x$amount * $price"
    val preview: ReceiptPreview
        get() = ReceiptPreview(
            name!!,
            personNames = persons.map { it.name },
            itemSummaries = items.map { it.summary },
            totalSum = computeTotalSumOf(items)
        )

    fun addPerson(person: Person) {
        persons = persons + person
    }

    fun hasPersonWithName(name: String): Boolean {
        return persons.find { it.name == name } != null
    }

    fun addItem(item: ReceiptItem) {
        items = items + item
    }

    fun hasItemWithName(name: String): Boolean {
        return items.find { it.name == name } != null
    }

    fun addConsumption(consumption: Consumption) {
        consumptions = consumptions + consumption
    }

    private fun computeTotalSumOf(items: List<ReceiptItem>): Double {
        return items.fold(0.0) { acc, item -> acc + item.amount * item.price }
    }

    fun execute() {
        ReceiptRepository.saveReceipt(
            Receipt(
                name = name!!,
                totalSum = computeTotalSumOf(items),
                persons, items, consumptions
            ),
            ownerToken
        )
    }
}
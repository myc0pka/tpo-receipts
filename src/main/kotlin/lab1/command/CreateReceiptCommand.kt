package lab1.command

import lab1.model.Person
import lab1.model.ReceiptItem

class CreateReceiptCommand {

    var name: String? = null
    private var persons: List<Person> = emptyList()
    private var items: List<ReceiptItem> = emptyList()

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
}
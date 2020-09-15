package lab1.command

import lab1.model.Person

class CreateReceiptCommand {

    var name: String? = null
    private var persons: List<Person> = emptyList()
    val personCount: Int
        get() = persons.size

    fun addPerson(person: Person) {
        persons = persons + person
    }
}
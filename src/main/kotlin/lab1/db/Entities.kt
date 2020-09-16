package lab1.db

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class ReceiptEntity(id: EntityID<Int>) : IntEntity(id) {

    companion object : IntEntityClass<ReceiptEntity>(Receipts)

    var ownerToken by Receipts.ownerToken
    var name by Receipts.name
    var totalSum by Receipts.totalSum
    val persons by PersonEntity referrersOn Persons.receipt
    val items by ReceiptItemEntity referrersOn ReceiptItems.receipt
}

class PersonEntity(id: EntityID<Int>) : IntEntity(id) {

    companion object : IntEntityClass<PersonEntity>(Persons)

    var receipt by ReceiptEntity referencedOn Persons.receipt
    var name by Persons.name
    val consumptions by ConsumptionEntity referrersOn Consumptions.person
}

class ReceiptItemEntity(id: EntityID<Int>) : IntEntity(id) {

    companion object : IntEntityClass<ReceiptItemEntity>(ReceiptItems)

    var receipt by ReceiptEntity referencedOn ReceiptItems.receipt
    var name by ReceiptItems.name
    var amount by ReceiptItems.amount
    var price by ReceiptItems.price
}

class ConsumptionEntity(id: EntityID<Int>) : IntEntity(id) {

    companion object : IntEntityClass<ConsumptionEntity>(Consumptions)

    var person by PersonEntity referencedOn Consumptions.person
    var item by ReceiptItemEntity referencedOn Consumptions.item
    var amount by Consumptions.amount
}
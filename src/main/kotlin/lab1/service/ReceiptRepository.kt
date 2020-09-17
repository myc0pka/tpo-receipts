package lab1.service

import lab1.db.Consumptions
import lab1.db.Persons
import lab1.db.ReceiptItems
import lab1.db.Receipts
import lab1.model.Receipt
import lab1.model.ReceiptItem
import lab1.model.ReceiptName
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object ReceiptRepository {

    fun saveReceipt(receipt: Receipt, ownerToken: String) {
        transaction {
            val generatedReceiptId = Receipts.insert {
                it[this.ownerToken] = ownerToken
                it[name] = receipt.name
                it[totalSum] = receipt.totalSum
            } get Receipts.id
            val personsWithId = receipt.persons.map { person ->
                val generatedId = Persons.insert {
                    it[name] = person.name
                    it[this.receipt] = generatedReceiptId
                } get Persons.id
                person.copy(id = generatedId.value)
            }
            val itemsWithId = receipt.items.map { item ->
                val generatedId = ReceiptItems.insert {
                    it[name] = item.name
                    it[amount] = item.amount
                    it[price] = item.price
                    it[this.receipt] = generatedReceiptId
                } get ReceiptItems.id
                item.copy(id = generatedId.value)
            }
            receipt.consumptions.forEach { consumption ->
                val personIdValue = personsWithId.find { it.name == consumption.person.name }!!.id
                val itemIdValue = itemsWithId.find { it.name == consumption.item.name }!!.id
                Consumptions.insert {
                    it[this.receipt] = generatedReceiptId
                    it[person] = EntityID(personIdValue, Persons)
                    it[item] = EntityID(itemIdValue, ReceiptItems)
                    it[amount] = consumption.amount
                }
            }
        }
    }

    fun getReceiptNamesByToken(ownerToken: String): List<ReceiptName> {
        val queryResult = Receipts.slice(Receipts.id, Receipts.name).select { Receipts.ownerToken eq ownerToken }
        return queryResult.map { ReceiptName(it[Receipts.name], it[Receipts.id].value) }
    }

    fun getReceiptItemsByReceiptId(receiptId: Int): List<ReceiptItem> {
        val queryResult = ReceiptItems.select { ReceiptItems.receipt eq EntityID(receiptId, Receipts) }
        return queryResult.map { ReceiptItem(it[ReceiptItems.name], it[ReceiptItems.amount], it[ReceiptItems.price]) }
    }
}
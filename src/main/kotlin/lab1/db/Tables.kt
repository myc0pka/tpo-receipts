package lab1.db

import lab1.service.ENCODED_TOKEN_SIZE
import lab1.ui.menu.PERSON_NAME_MAX_LENGTH
import lab1.ui.menu.RECEIPT_ITEM_NAME_MAX_LENGTH
import lab1.ui.menu.RECEIPT_NAME_MAX_LENGTH
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object Receipts : IntIdTable() {

    val ownerToken = varchar("owner_token", length = ENCODED_TOKEN_SIZE + 1)
    val name = varchar("name", length = RECEIPT_NAME_MAX_LENGTH + 1)
    val totalSum = double("total_sum")
}

object Persons : IntIdTable() {

    val receipt = reference("receipt", Receipts, onDelete = ReferenceOption.CASCADE)
    val name = varchar("name", length = PERSON_NAME_MAX_LENGTH + 1)
}

object ReceiptItems : IntIdTable() {

    val receipt = reference("receipt", Receipts, onDelete = ReferenceOption.CASCADE)
    val name = varchar("name", length = RECEIPT_ITEM_NAME_MAX_LENGTH + 1)
    val amount = integer("amount")
    val price = double("price")
}

object Consumptions : IntIdTable() {

    val person = reference("person", Persons, onDelete = ReferenceOption.CASCADE)
    val item = reference("item", ReceiptItems)
    val amount = integer("amount")
}
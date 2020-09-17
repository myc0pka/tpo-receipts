package lab1.model

data class Receipt(
    val name: String,
    val totalSum: Double,
    val persons: List<Person>,
    val items: List<ReceiptItem>,
    val consumptions: List<Consumption>,
    val id: Int = 0
)
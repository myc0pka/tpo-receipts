package lab1.model

data class ReceiptPreview(
    val name: String,
    val personNames: List<String>,
    val itemSummaries: List<String>,
    val totalSum: Double
)
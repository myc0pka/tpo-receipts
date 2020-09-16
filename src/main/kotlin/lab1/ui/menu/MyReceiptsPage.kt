package lab1.ui.menu

import lab1.db.ReceiptEntity

class MyReceiptsPage(
    private val receiptEntities: List<ReceiptEntity>
) : OptionsMenuPage<MyReceiptsPage.Option>(
    options = receiptEntities.map { Option(it) }
) {

    class Option(val receiptEntity: ReceiptEntity) : MenuOption {

        override val text: String
            get() = receiptEntity.name
    }

    override fun handleOptionInput(option: Option): Action {
        return Action.Stub("Show receipt page")
    }
}
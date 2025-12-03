package bibliotecaG.data.model

data class Order(
    val id: String,
    val userId: String,
    val total: Double,
    val status: String,
    val createdAt: String? = null,

    val items: List<OrderItem> = emptyList()
)
package bibliotecaG.data.model

data class Order(
    val id: String,
    val userId: String,
    val total: Double,
    val status: String, // "pendiente", "pagado", "cancelado"
    val createdAt: String? = null
)
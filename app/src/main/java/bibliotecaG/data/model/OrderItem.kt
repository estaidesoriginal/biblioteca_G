package bibliotecaG.data.model

import com.google.gson.annotations.SerializedName

data class OrderItem(
    val id: Long,
    val productId: String,
    val quantity: Int,
    val priceAtPurchase: Double
)
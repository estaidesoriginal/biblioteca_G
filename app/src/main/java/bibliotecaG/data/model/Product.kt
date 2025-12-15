package bibliotecaG.data.model

import com.google.gson.annotations.SerializedName
import java.util.UUID

data class Product(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val price: Double,

    // CAMBIO: Ahora es una lista, igual que en Game.tags
    // Esto permitirá productos con múltiples categorías (ej: "Ropa", "Accesorios")
    val categories: List<String> = emptyList(),

    @SerializedName("image_url")
    val imageUrl: String? = null,
    val stock: Int = 0
)
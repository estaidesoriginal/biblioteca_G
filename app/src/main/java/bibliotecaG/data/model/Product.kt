package bibliotecaG.data.model

import com.google.gson.annotations.SerializedName
import java.util.UUID

data class Product(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    // Usamos @SerializedName para leer el JSON de la API (snake_case)
    @SerializedName("image_url")
    val imageUrl: String? = null,
    val stock: Int = 0
)
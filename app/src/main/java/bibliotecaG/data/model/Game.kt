package bibliotecaG.data.model

import com.google.gson.annotations.SerializedName
import java.util.UUID

data class Game(
    // La API debe devolver el ID como String (UUID)
    val id: String = UUID.randomUUID().toString(),

    val title: String,
    val description: String,

    // Asumimos que la API devuelve un Array JSON real: ["RPG", "Accion"]
    val tags: List<String> = emptyList(),

    // --- Mapeo de Nombres (DB snake_case <-> Kotlin camelCase) ---

    // En la DB es "image_url", en Kotlin es "imageUrl"
    @SerializedName("image_url")
    val imageUrl: String? = null,

    // En la DB es "external_links", en Kotlin es "externalLinks"
    @SerializedName("external_links")
    val externalLinks: List<String> = emptyList(),

    // --- Lógica de Protección ---
    // En la DB es "protection_status_id" (INT)
    // 1 = Protegido (Solo Admin), 2 = Desprotegido (Cualquiera)
    @SerializedName("protection_status_id")
    val protectionStatusId: Int = 2
)

package bibliotecaG.data.model

import com.google.gson.annotations.SerializedName
import java.util.UUID

data class Game(

    val id: String = UUID.randomUUID().toString(),

    val title: String,
    val description: String,

    val tags: List<String> = emptyList(),

    @SerializedName("image_url")
    val imageUrl: String? = null,

    @SerializedName("external_links")
    val externalLinks: List<String> = emptyList(),

    @SerializedName("protection_status_id")
    val protectionStatusId: Int = 2
)

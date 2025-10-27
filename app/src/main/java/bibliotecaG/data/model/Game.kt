package bibliotecaG.data.model

import java.util.UUID

data class Game(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val tags: List<String> = emptyList(),
    val imageUrl: String? = null,
    val externalLinks: List<String> = emptyList(),
    val creatorRole: String = "USER"
)

package bibliotecaG.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val tags: String,
    val imageUrl: String?,
    val externalLinks: String?,
    val creatorRole: String
)

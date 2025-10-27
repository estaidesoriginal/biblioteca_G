package bibliotecaG.data.repository

import bibliotecaG.data.local.GameDao
import bibliotecaG.data.local.GameEntity
import bibliotecaG.data.model.Game
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Profeee atencion aqui lea esta plana junto con sus anotaciones, es por el comentario de la clase pasada : "donde esta la id de cada juego"
 Repositorio maneja la logica de datos
 es la unica funte de verdad para /viewmodel/
 recibe el Dao para acceder a la base de datos
 */
class GameRepository(private val gameDao: GameDao) {
    /**
     Obtiene todos los juegos de Room (GameEntity) y los mapea a
     objetos del modelo de dominio (/model/Game) para que UI  los consuma
     */
    val games: Flow<List<Game>> = gameDao.getAllGames()
        .map { entityList ->
            entityList.map { entity ->
                // Mapeo de Entity
                Game(
                    id = entity.id,
                    title = entity.title,
                    description = entity.description,
                    tags = entity.tags.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                    imageUrl = entity.imageUrl,
                    externalLinks = entity.externalLinks?.split(",")?.map { it.trim() } ?: emptyList(),
                    creatorRole = entity.creatorRole
                )
            }
        }

    /**
      Mapea un Game (Model) a un GameEntity y lo inserta en Room
     */
    suspend fun addGame(game: Game) {
        gameDao.insertGame(game.toEntity())
    }

    /**
      Mapea un Game (Model) a un GameEntity y lo actualiza en Room
     */
    suspend fun updateGame(game: Game) {
        gameDao.updateGame(game.toEntity())
    }

    /**
      Mapea un Game (Model) a un GameEntity y lo elimina de Room
     */
    suspend fun deleteGame(game: Game) {
        gameDao.deleteGame(game.toEntity())
    }

    /**
      Función helper para convertir Model -> Entity
     */
    private fun Game.toEntity(): GameEntity {
        return GameEntity(
            id = this.id,
            title = this.title,
            description = this.description,
            tags = this.tags.joinToString(","),
            imageUrl = this.imageUrl,
            externalLinks = this.externalLinks.joinToString(","),
            creatorRole = this.creatorRole // <-- ¡AÑADIDO!
        )
    }
}
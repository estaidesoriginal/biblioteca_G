package bibliotecaG.data.repository

import bibliotecaG.data.local.GameDao
import bibliotecaG.data.local.GameEntity
import bibliotecaG.data.model.Game
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repositorio que maneja la lógica de datos.
 * Es la ÚNICA fuente de verdad para el ViewModel.
 * Recibe el DAO para acceder a la base de datos.
 */
class GameRepository(private val gameDao: GameDao) {

    /**
     * Obtiene todos los juegos de Room (GameEntity) y los mapea a
     * objetos del modelo de dominio (Game) para que la UI los consuma.
     */
    val games: Flow<List<Game>> = gameDao.getAllGames()
        .map { entityList ->
            entityList.map { entity ->
                // Mapeo de Entity -> Model
                Game(
                    id = entity.id,
                    title = entity.title,
                    description = entity.description,
                    tags = entity.tags.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                    imageUrl = entity.imageUrl,
                    externalLinks = entity.externalLinks?.split(",")?.map { it.trim() } ?: emptyList()
                )
            }
        }

    /**
     * Mapea un Game (Model) a un GameEntity y lo inserta en Room.
     */
    suspend fun addGame(game: Game) {
        gameDao.insertGame(game.toEntity())
    }

    /**
     * Mapea un Game (Model) a un GameEntity y lo actualiza en Room.
     */
    suspend fun updateGame(game: Game) {
        gameDao.updateGame(game.toEntity())
    }

    /**
     * Mapea un Game (Model) a un GameEntity y lo elimina de Room.
     */
    suspend fun deleteGame(game: Game) {
        gameDao.deleteGame(game.toEntity())
    }

    /**
     * Función helper para convertir Model -> Entity
     * (Esta lógica estaba antes en el ViewModel, ahora está en el Repositorio)
     */
    private fun Game.toEntity(): GameEntity {
        return GameEntity(
            id = this.id,
            title = this.title,
            description = this.description,
            tags = this.tags.joinToString(","),
            imageUrl = this.imageUrl,
            externalLinks = this.externalLinks.joinToString(",")
        )
    }
}
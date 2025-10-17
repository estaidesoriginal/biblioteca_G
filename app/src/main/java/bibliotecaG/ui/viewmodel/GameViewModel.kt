package bibliotecaG.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import bibliotecaG.data.local.GameDao
import bibliotecaG.data.local.GameEntity
import bibliotecaG.data.model.Game

class GameViewModel(private val dao: GameDao) : ViewModel() {

    val games: StateFlow<List<Game>> = dao.getAllGames()
        .map { list ->
            list.map { entity ->
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
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addGame(game: Game) = viewModelScope.launch {
        dao.insertGame(
            GameEntity(
                id = game.id,
                title = game.title,
                description = game.description,
                tags = game.tags.joinToString(","),
                imageUrl = game.imageUrl,
                externalLinks = game.externalLinks.joinToString(",")
            )
        )
    }

    fun updateGame(game: Game) = viewModelScope.launch {
        dao.updateGame(
            GameEntity(
                id = game.id,
                title = game.title,
                description = game.description,
                tags = game.tags.joinToString(","),
                imageUrl = game.imageUrl,
                externalLinks = game.externalLinks.joinToString(",")
            )
        )
    }

    fun deleteGame(game: Game) = viewModelScope.launch {
        dao.deleteGame(
            GameEntity(
                id = game.id,
                title = game.title,
                description = game.description,
                tags = game.tags.joinToString(","),
                imageUrl = game.imageUrl,
                externalLinks = game.externalLinks.joinToString(",")
            )
        )
    }

    fun search(query: String): List<Game> {
        return games.value.filter {
            it.title.contains(query, true) ||
                    it.description.contains(query, true) ||
                    it.tags.any { tag -> tag.contains(query, true) }
        }
    }
}
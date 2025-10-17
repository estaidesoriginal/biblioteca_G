package bibliotecaG.data.repository

import bibliotecaG.data.model.Game
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object GameRepository {
    private val _games = MutableStateFlow<List<Game>>(emptyList())
    val games = _games.asStateFlow()

    fun addGame(game: Game) {
        _games.value = _games.value + game
    }

    fun searchGames(query: String): List<Game> {
        val q = query.trim()
        if (q.isEmpty()) return _games.value
        return _games.value.filter { game ->
            game.title.contains(q, ignoreCase = true) ||
                    game.description.contains(q, ignoreCase = true) ||
                    game.tags.any { it.contains(q, ignoreCase = true) }
        }
    }

    fun getGameById(id: String): Game? = _games.value.find { it.id == id }
}
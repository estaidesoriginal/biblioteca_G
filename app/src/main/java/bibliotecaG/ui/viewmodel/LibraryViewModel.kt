package bibliotecaG.ui.viewmodel

import androidx.lifecycle.ViewModel
import bibliotecaG.data.model.Game
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf

class LibraryViewModel : ViewModel() { // Lista observable de juegos/tomos
    private val _games = mutableStateListOf<Game>()
    val games: List<Game> get() = _games

    // Texto actual de búsqueda
    var searchQuery = mutableStateOf("")
        private set

    // Juego seleccionado (para la pantalla de detalles)
    var selectedGame = mutableStateOf<Game?>(null)
        private set

    // --- Funciones principales ---

    fun addGame(game: Game) {
        _games.add(game)
    }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun getFilteredGames(): List<Game> {
        val query = searchQuery.value.lowercase()
        return if (query.isEmpty()) {
            _games
        } else {
            _games.filter { game ->
                game.title.lowercase().contains(query) ||
                        game.tags.any { it.lowercase().contains(query) }
            }
        }
    }

    fun selectGame(game: Game) {
        selectedGame.value = game
    }

    fun clearSelection() {
        selectedGame.value = null
    }
    fun updateGame(updatedGame: Game) {
        val index = _games.indexOfFirst { it.id == updatedGame.id }
        if (index != -1) {
            _games[index] = updatedGame
        }
    }

    fun deleteGame(game: Game) {
        _games.remove(game)
        if (selectedGame.value?.id == game.id) {
            clearSelection()
        }
    }
}

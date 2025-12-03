package bibliotecaG.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bibliotecaG.data.model.Game
import bibliotecaG.data.repository.GameRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GameViewModel(private val repository: GameRepository) : ViewModel() {

    // Observamos directamente la lista que el repositorio mantiene en memoria
    val games: StateFlow<List<Game>> = repository.games

    // Opcional: Podrías exponer 'isLoading' o 'error' si quieres mostrarlos en la UI
    // val isLoading = repository.isLoading

    /**
     * Llama al repositorio para que descargue los datos frescos de la API.
     */
    fun refreshGames() = viewModelScope.launch {
        repository.fetchGames()
    }

    fun addGame(game: Game) = viewModelScope.launch {
        repository.addGame(game)
    }

    fun updateGame(game: Game) = viewModelScope.launch {
        repository.updateGame(game)
    }

    fun deleteGame(game: Game) = viewModelScope.launch {
        repository.deleteGame(game)
    }

    fun search(query: String): List<Game> {
        // La búsqueda sigue siendo local sobre la lista descargada
        return games.value.filter {
            it.title.contains(query, true) ||
                    it.description.contains(query, true) ||
                    it.tags.any { tag -> tag.contains(query, true) }
        }
    }
}
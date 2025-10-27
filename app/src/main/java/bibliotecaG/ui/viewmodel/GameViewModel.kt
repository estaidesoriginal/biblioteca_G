package bibliotecaG.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import bibliotecaG.data.model.Game
import bibliotecaG.data.repository.GameRepository

class GameViewModel(private val repository: GameRepository) : ViewModel() {

    // Profeeee si esta leyendo esto pasese por la carpeta "/data/repository/" ahí le hablamos mas de esto
    // obtenemos los juegos directamente del repositorio (ya están mapeados)
    val games: StateFlow<List<Game>> = repository.games
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

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
        return games.value.filter {
            it.title.contains(query, true) ||
                    it.description.contains(query, true) ||
                    it.tags.any { tag -> tag.contains(query, true) }
        }
    }
}
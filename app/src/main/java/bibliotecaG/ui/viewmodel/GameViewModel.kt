package bibliotecaG.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bibliotecaG.data.model.Game
import bibliotecaG.data.repository.GameRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GameViewModel(private val repository: GameRepository) : ViewModel() {

    val games: StateFlow<List<Game>> = repository.games

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
        return games.value.filter {
            it.title.contains(query, true) ||
                    it.description.contains(query, true) ||
                    it.tags.any { tag -> tag.contains(query, true) }
        }
    }
}
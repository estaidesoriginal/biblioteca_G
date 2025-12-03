package bibliotecaG.data.repository

import bibliotecaG.data.model.Game
import bibliotecaG.data.remote.GameApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameRepository(private val api: GameApiService) {

    private val _games = MutableStateFlow<List<Game>>(emptyList())
    val games: StateFlow<List<Game>> = _games.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    suspend fun fetchGames() {
        _isLoading.value = true
        try {
            val remoteGames = api.getAllGames()
            _games.value = remoteGames
            _error.value = null
        } catch (e: Exception) {
            _error.value = "Error al cargar: ${e.message}"
            e.printStackTrace()
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun addGame(game: Game) {
        _isLoading.value = true
        try {
            api.createGame(game)
            fetchGames()
        } catch (e: Exception) {
            _error.value = "Error al crear: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun updateGame(game: Game) {
        _isLoading.value = true
        try {
            api.updateGame(game.id, game)
            fetchGames()
        } catch (e: Exception) {
            _error.value = "Error al actualizar: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun deleteGame(game: Game) {
        _isLoading.value = true
        try {
            api.deleteGame(game.id)
            fetchGames()
        } catch (e: Exception) {
            _error.value = "Error al borrar: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }
}
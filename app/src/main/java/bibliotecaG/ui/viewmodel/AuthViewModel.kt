package bibliotecaG.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bibliotecaG.data.local.SessionManager // Importante
import bibliotecaG.data.model.LoginRequest
import bibliotecaG.data.model.RegisterRequest
import bibliotecaG.data.model.User
import bibliotecaG.data.remote.GameApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Agregamos SessionManager al constructor
class AuthViewModel(
    private val api: GameApiService,
    private val sessionManager: SessionManager
) : ViewModel() {

    // Inicializamos _currentUser con lo que haya en la memoria (puede ser null o el usuario guardado)
    private val _currentUser = MutableStateFlow<User?>(sessionManager.getUser())
    val currentUser = _currentUser.asStateFlow()

    // Este StateFlow se actualiza automáticamente basado en currentUser
    val currentUserRole: StateFlow<String?> = _currentUser
        .map { user -> user?.role }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = sessionManager.getUser()?.role // Valor inicial desde memoria
        )

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun login(email: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = api.login(LoginRequest(email, pass))
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!.user
                    val token = response.body()!!.token

                    // Guardamos en memoria interna
                    sessionManager.saveUser(user)
                    if (token != null) sessionManager.saveToken(token)

                    // Actualizamos el estado de la app
                    _currentUser.value = user
                    onSuccess()
                } else {
                    _error.value = "Credenciales inválidas"
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(name: String, email: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = api.register(RegisterRequest(name, email, pass))
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!.user

                    // Guardamos sesión también al registrarse
                    sessionManager.saveUser(user)

                    _currentUser.value = user
                    onSuccess()
                } else {
                    _error.value = "Error en el registro"
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        // Borramos la memoria interna
        sessionManager.clearSession()
        _currentUser.value = null
    }

    fun clearError() {
        _error.value = null
    }
}
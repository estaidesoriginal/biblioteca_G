package bibliotecaG.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel : ViewModel() {

    private val _currentUserRole = MutableStateFlow<String?>(null)
    val currentUserRole = _currentUserRole.asStateFlow()

    // Credenciales "forzadas"
    private val adminCredentials = mapOf(
        "admin@gmail.com" to "Password"
    )
    private val userCredentials = mapOf(
        "usuario@gmail.com" to "Password"
    )
    fun login(email: String, pass: String): Boolean {
        if (adminCredentials[email] == pass) {
            _currentUserRole.value = "ADMIN"
            return true
        }
        if (userCredentials[email] == pass) {
            _currentUserRole.value = "USER"
            return true
        }
        return false
    }
    fun logout() {
        _currentUserRole.value = null
    }
}
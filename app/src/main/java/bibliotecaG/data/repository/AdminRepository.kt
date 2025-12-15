package bibliotecaG.data.repository

import bibliotecaG.data.model.Order
import bibliotecaG.data.model.User
import bibliotecaG.data.remote.GameApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AdminRepository(private val api: GameApiService) {

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders = _orders.asStateFlow()

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users = _users.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    suspend fun fetchOrders() {
        try {
            val remoteOrders = api.getAllOrders()
            _orders.value = remoteOrders.sortedByDescending { it.id }
        } catch (e: Exception) {
            _error.value = "Error órdenes: ${e.localizedMessage}"
        }
    }

    suspend fun updateOrderStatus(orderId: String, newStatus: String) {
        try {
            val body = mapOf("status" to newStatus)
            val response = api.updateOrderStatus(orderId, body)
            if (response.isSuccessful) {
                fetchOrders()
            } else {
                _error.value = "Error al actualizar: ${response.code()}"
            }
        } catch (e: Exception) {
            _error.value = "Error de conexión: ${e.localizedMessage}"
        }
    }

    suspend fun fetchUsers() {
        try {
            val remoteUsers = api.getAllUsers()
            _users.value = remoteUsers
        } catch (e: Exception) {
            _error.value = "Error usuarios: ${e.localizedMessage}"
        }
    }

    suspend fun deleteUser(userId: String) {
        try {
            api.deleteUser(userId)
            fetchUsers()
        } catch (e: Exception) {
            _error.value = "Error al borrar usuario: ${e.localizedMessage}"
        }
    }

    suspend fun updateUserRole(userId: String, newRole: String) {
        try {
            val body = mapOf("role" to newRole)
            api.updateUserRole(userId, body)
            fetchUsers() 
        } catch (e: Exception) {
            _error.value = "Error al cambiar rol: ${e.localizedMessage}"
        }
    }

}

package bibliotecaG.data.repository

import bibliotecaG.data.model.Order
import bibliotecaG.data.remote.GameApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AdminRepository(private val api: GameApiService) {

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders = _orders.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    suspend fun fetchOrders() {
        try {
            val remoteOrders = api.getAllOrders()
            _orders.value = remoteOrders
        } catch (e: Exception) {
            _error.value = "Error al cargar órdenes: ${e.localizedMessage}"
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
}
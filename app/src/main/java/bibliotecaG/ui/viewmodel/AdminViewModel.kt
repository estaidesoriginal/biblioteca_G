package bibliotecaG.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bibliotecaG.data.repository.AdminRepository
import kotlinx.coroutines.launch

class AdminViewModel(private val repository: AdminRepository) : ViewModel() {

    val orders = repository.orders
    val users = repository.users
    val error = repository.error

    // --- ÓRDENES ---
    fun loadOrders() = viewModelScope.launch {
        repository.fetchOrders()
    }

    fun updateStatus(orderId: String, newStatus: String) = viewModelScope.launch {
        repository.updateOrderStatus(orderId, newStatus)
    }

    // --- USUARIOS ---
    fun loadUsers() = viewModelScope.launch {
        repository.fetchUsers()
    }

    fun deleteUser(userId: String) = viewModelScope.launch {
        repository.deleteUser(userId)
    }

    // AQUÍ SE USA 'newRole'
    fun changeUserRole(userId: String, newRole: String) = viewModelScope.launch {
        repository.updateUserRole(userId, newRole)
    }
}
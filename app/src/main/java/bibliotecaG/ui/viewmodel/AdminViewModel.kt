package bibliotecaG.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bibliotecaG.data.repository.AdminRepository
import kotlinx.coroutines.launch

class AdminViewModel(private val repository: AdminRepository) : ViewModel() {

    val orders = repository.orders
    val error = repository.error

    fun loadOrders() = viewModelScope.launch {
        repository.fetchOrders()
    }

    fun updateStatus(orderId: String, newStatus: String) = viewModelScope.launch {
        repository.updateOrderStatus(orderId, newStatus)
    }
}
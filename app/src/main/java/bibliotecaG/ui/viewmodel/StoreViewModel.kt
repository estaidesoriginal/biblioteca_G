package bibliotecaG.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bibliotecaG.data.model.Product
import bibliotecaG.data.repository.StoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class StoreViewModel(private val repository: StoreRepository) : ViewModel() {

    val products = repository.products
    val cart = repository.cart

    private val _purchaseSuccess = MutableStateFlow<String?>(null)
    val purchaseSuccess = _purchaseSuccess.asStateFlow()

    private val _lastPurchaseTotal = MutableStateFlow(0.0)
    val lastPurchaseTotal = _lastPurchaseTotal.asStateFlow()

    fun loadProducts() = viewModelScope.launch {
        repository.fetchProducts()
    }

    // --- ACCIONES DE ADMIN ---
    fun addProduct(product: Product) = viewModelScope.launch {
        repository.createProduct(product)
    }

    fun updateProduct(product: Product) = viewModelScope.launch {
        repository.updateProduct(product)
    }

    fun deleteProduct(product: Product) = viewModelScope.launch {
        repository.deleteProduct(product.id)
    }

    // --- ACCIONES DE CARRITO ---
    fun addToCart(product: Product) = repository.addToCart(product)
    fun removeFromCart(product: Product) = repository.removeFromCart(product)
    fun increaseQty(product: Product) = repository.updateQuantity(product, true)
    fun decreaseQty(product: Product) = repository.updateQuantity(product, false)
    fun getTotal() = repository.getCartTotal()

    // --- CORRECCIÓN: Ahora recibe userId ---
    fun confirmPurchase(userId: String) = viewModelScope.launch {
        _lastPurchaseTotal.value = getTotal()

        // Pasamos el userId al repositorio
        val success = repository.sendPurchase(userId)

        if (success) {
            _purchaseSuccess.value = "ORD-${UUID.randomUUID().toString().take(8).uppercase()}"
        }
    }

    fun dismissPurchasePopup() {
        _purchaseSuccess.value = null
        _lastPurchaseTotal.value = 0.0
    }

    fun searchProducts(query: String): List<Product> {
        val q = query.lowercase()
        return products.value.filter {
            it.name.lowercase().contains(q) || it.category.lowercase().contains(q)
        }
    }
}
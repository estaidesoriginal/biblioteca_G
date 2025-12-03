package bibliotecaG.data.repository

import bibliotecaG.data.model.CartItem
import bibliotecaG.data.model.Product
import bibliotecaG.data.remote.GameApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class StoreRepository(private val api: GameApiService) {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products = _products.asStateFlow()

    private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
    val cart = _cart.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    suspend fun fetchProducts() {
        try {
            val remoteProducts = api.getAllProducts()
            _products.value = remoteProducts
            _error.value = null
        } catch (e: Exception) {
            _error.value = "Error al cargar productos: ${e.localizedMessage}"
        }
    }
    suspend fun createProduct(product: Product) {
        try {
            api.createProduct(product)
            fetchProducts()
        } catch (e: Exception) {
            _error.value = "Error al crear: ${e.localizedMessage}"
        }
    }

    suspend fun updateProduct(product: Product) {
        try {
            api.updateProduct(product.id, product)
            fetchProducts()
        } catch (e: Exception) {
            _error.value = "Error al actualizar: ${e.localizedMessage}"
        }
    }

    suspend fun deleteProduct(productId: String) {
        try {
            api.deleteProduct(productId)
            fetchProducts()
        } catch (e: Exception) {
            _error.value = "Error al eliminar: ${e.localizedMessage}"
        }
    }
    fun addToCart(product: Product) {
        _cart.update { currentItems ->
            val existing = currentItems.find { it.product.id == product.id }

            if (existing != null) {
                if (existing.quantity < product.stock) {
                    currentItems.map {
                        if (it.product.id == product.id) it.copy(quantity = it.quantity + 1) else it
                    }
                } else {
                    currentItems
                }
            } else {
                if (product.stock > 0) {
                    currentItems + CartItem(product, 1)
                } else {
                    currentItems
                }
            }
        }
    }

    fun removeFromCart(product: Product) {
        _cart.update { it.filter { item -> item.product.id != product.id } }
    }

    fun updateQuantity(product: Product, increase: Boolean) {
        _cart.update { currentItems ->
            currentItems.map { item ->
                if (item.product.id == product.id) {
                    if (increase) {
                        if (item.quantity < product.stock) {
                            item.copy(quantity = item.quantity + 1)
                        } else {
                            item
                        }
                    } else {
                        val newQty = item.quantity - 1
                        item.copy(quantity = newQty.coerceAtLeast(1))
                    }
                } else {
                    item
                }
            }
        }
    }

    fun clearCart() {
        _cart.value = emptyList()
    }

    fun getCartTotal(): Double = _cart.value.sumOf { it.total }

    suspend fun sendPurchase(userId: String): Boolean {
        return try {
            val currentItems = _cart.value
            if (currentItems.isNotEmpty()) {
                val response = api.confirmPurchase(currentItems, userId)
                if (response.isSuccessful) {
                    clearCart()
                    true
                } else {
                    _error.value = "Error en el servidor: ${response.code()}"
                    false
                }
            } else {
                false
            }
        } catch (e: Exception) {
            _error.value = "Error de conexión al comprar: ${e.localizedMessage}"
            false
        }
    }
}
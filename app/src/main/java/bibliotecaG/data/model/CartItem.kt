package bibliotecaG.data.model

data class CartItem(
    val product: Product,
    var quantity: Int
) {
    val total: Double
        get() = product.price * quantity
}
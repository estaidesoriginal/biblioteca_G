package bibliotecaG.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Library : BottomNavItem("home", "Biblioteca", Icons.Default.Book)
    object Store : BottomNavItem("store", "Tienda", Icons.Default.ShoppingBag)
    object Cart : BottomNavItem("cart", "Carrito", Icons.Default.ShoppingCart)
    object Profile : BottomNavItem("profile", "Perfil", Icons.Default.Person)
    object Admin : BottomNavItem("admin_panel", "Admin", Icons.Default.AdminPanelSettings)
}

package bibliotecaG.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Ballot
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    // NUEVO: Pantalla de Inicio (Bienvenida/Explicación)
    object Start : BottomNavItem("start", "Inicio", Icons.Default.Home)

    // MODIFICADO: Antes era "home", ahora es "library" con icono de Lista
    object Library : BottomNavItem("library", "Juegos", Icons.Default.Book)

    object Store : BottomNavItem("store", "Tienda", Icons.Default.ShoppingBag)
    object Cart : BottomNavItem("cart", "Carrito", Icons.Default.ShoppingCart)
    object Profile : BottomNavItem("profile", "Perfil", Icons.Default.Person)
    object Admin : BottomNavItem("admin_panel", "Compras", Icons.Default.Ballot)
}

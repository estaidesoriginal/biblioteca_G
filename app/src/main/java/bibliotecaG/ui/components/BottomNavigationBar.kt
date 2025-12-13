package bibliotecaG.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import bibliotecaG.ui.navigation.BottomNavItem
import bibliotecaG.ui.viewmodel.UserRoles

@Composable
fun BottomNavigationBar(
    navController: NavController,
    userRole: String?
) {
    val items = mutableListOf<BottomNavItem>()

    // 1. Items fijos
    items.add(BottomNavItem.Start)
    items.add(BottomNavItem.Library)
    items.add(BottomNavItem.Store)

    // 2. Carrito (Solo USER o Invitado)
    if (userRole == null || userRole == UserRoles.USER) {
        items.add(BottomNavItem.Cart)
    }

    // 3. Perfil (Siempre)
    items.add(BottomNavItem.Profile)

    // 4. Admin
    if (userRole == UserRoles.ADMIN || userRole == UserRoles.MANAGER) {
        items.add(BottomNavItem.Admin)
    }

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        // CORRECCIÓN CRÍTICA:
                        // Simplificamos la navegación para evitar conflictos de estado
                        // al cambiar de rol (Invitado -> User).

                        // 1. Volvemos al inicio del grafo para no acumular pantallas
                        popUpTo(navController.graph.findStartDestination().id) {
                            // saveState = true  <-- ELIMINADO: No guardamos estado para evitar conflictos
                        }

                        // 2. Evitamos duplicados si se pulsa varias veces
                        launchSingleTop = true

                        // 3. restoreState = true <-- ELIMINADO: Forzamos recarga de la pantalla
                        // Esto asegura que el Perfil se actualice correctamente con los nuevos datos
                    }
                }
            )
        }
    }
}
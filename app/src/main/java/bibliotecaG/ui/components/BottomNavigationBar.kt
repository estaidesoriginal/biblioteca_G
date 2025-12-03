package bibliotecaG.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import bibliotecaG.ui.navigation.BottomNavItem

@Composable
fun BottomNavigationBar(
    navController: NavController,
    userRole: String?
) {

    val items = mutableListOf(
        BottomNavItem.Library,
        BottomNavItem.Store,
        BottomNavItem.Cart,
        BottomNavItem.Profile
    )

    if (userRole == "ADMIN") {
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
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
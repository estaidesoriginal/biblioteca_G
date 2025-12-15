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

    items.add(BottomNavItem.Start)
    items.add(BottomNavItem.Library)
    items.add(BottomNavItem.Store)

    if (userRole == null || userRole == UserRoles.USER) {
        items.add(BottomNavItem.Cart)
    }

    items.add(BottomNavItem.Profile)

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

                        popUpTo(navController.graph.findStartDestination().id) {
                        }

                        launchSingleTop = true

                    }
                }
            )
        }
    }

}

package bibliotecaG

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import bibliotecaG.data.model.User
import bibliotecaG.data.remote.RetrofitClient
import bibliotecaG.data.repository.AdminRepository
import bibliotecaG.data.repository.GameRepository
import bibliotecaG.data.repository.StoreRepository
import bibliotecaG.ui.components.BottomNavigationBar
import bibliotecaG.ui.screens.addGame.AddGameScreen
import bibliotecaG.ui.screens.admin.AdminPanelScreen
import bibliotecaG.ui.screens.cart.CartScreen
import bibliotecaG.ui.screens.detail.GameDetailScreen
import bibliotecaG.ui.screens.home.HomeScreen
import bibliotecaG.ui.screens.login.LoginScreen
import bibliotecaG.ui.screens.profile.ProfileScreen
import bibliotecaG.ui.screens.start.StartScreen
import bibliotecaG.ui.screens.store.AddProductScreen
import bibliotecaG.ui.screens.store.StoreScreen
import bibliotecaG.ui.theme.BibliotecaGTheme
import bibliotecaG.ui.theme.ThemeType
import bibliotecaG.ui.viewmodel.AdminViewModel
import bibliotecaG.ui.viewmodel.AuthViewModel
import bibliotecaG.ui.viewmodel.GameViewModel
import bibliotecaG.ui.viewmodel.StoreViewModel

class MainActivity : ComponentActivity() {

    private val gameRepository by lazy {
        GameRepository(RetrofitClient.apiService)
    }

    private val storeRepository by lazy {
        StoreRepository(RetrofitClient.apiService)
    }

    private val adminRepository by lazy {
        AdminRepository(RetrofitClient.apiService)
    }

    // --- FACTORIES ---
    private val gameViewModelFactory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return GameViewModel(gameRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    private val storeViewModelFactory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(StoreViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return StoreViewModel(storeRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    private val authViewModelFactory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AuthViewModel(RetrofitClient.apiService) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    private val adminViewModelFactory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AdminViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AdminViewModel(adminRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    private val gameViewModel: GameViewModel by viewModels { gameViewModelFactory }
    private val storeViewModel: StoreViewModel by viewModels { storeViewModelFactory }
    private val authViewModel: AuthViewModel by viewModels { authViewModelFactory }
    private val adminViewModel: AdminViewModel by viewModels { adminViewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var currentTheme by remember { mutableStateOf(ThemeType.DEFAULT) }

            BibliotecaGTheme(themeType = currentTheme) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()

                    val currentUser: User? by authViewModel.currentUser.collectAsState()
                    val currentUserRole: String? = currentUser?.role

                    LaunchedEffect(Unit) {
                        gameViewModel.refreshGames()
                        storeViewModel.loadProducts()
                    }

                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    // CORRECCIÓN: "library" en lugar de "home" en la lista de visibilidad
                    val showBottomBar = currentRoute in listOf("start", "library", "store", "cart", "admin_panel", "profile")

                    Scaffold(
                        bottomBar = {
                            if (showBottomBar) {
                                BottomNavigationBar(navController = navController, userRole = currentUserRole)
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = "start",
                            modifier = Modifier.padding(innerPadding)
                        ) {

                            // --- RUTA: INICIO ---
                            composable("start") {
                                StartScreen()
                            }

                            // --- RUTA: BIBLIOTECA (Ahora usamos "library" consistentemente) ---
                            composable("library") {
                                HomeScreen(navController, gameViewModel, authViewModel)
                            }

                            composable("store") {
                                StoreScreen(navController, storeViewModel, authViewModel)
                            }

                            composable("cart") {
                                CartScreen(navController, storeViewModel, authViewModel)
                            }

                            composable("admin_panel") {
                                AdminPanelScreen(navController, adminViewModel, currentUserRole)
                            }

                            composable("profile") {
                                ProfileScreen(
                                    authViewModel = authViewModel,
                                    currentTheme = currentTheme,
                                    onThemeChange = { newTheme -> currentTheme = newTheme },
                                    onLogout = {
                                        authViewModel.logout()
                                        // Redirigir al login y limpiar historial
                                        navController.navigate("login") {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    },
                                    onLoginRequest = {
                                        navController.navigate("login")
                                    }
                                )
                            }

                            // --- VISTAS SECUNDARIAS ---
                            composable("login") {
                                LoginScreen(
                                    authViewModel = authViewModel,
                                    onLoginSuccess = {
                                        // CORRECCIÓN: Redirigir a "library" (no "home")
                                        navController.navigate("library") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    },
                                    onBack = { navController.popBackStack() }
                                )
                            }

                            composable("addGame") {
                                AddGameScreen(
                                    gameToEdit = null,
                                    canModify = true,
                                    onSave = { game ->
                                        val statusId = if (currentUserRole == "ADMIN") 1 else 2
                                        val gameToSave = game.copy(protectionStatusId = statusId)
                                        gameViewModel.addGame(gameToSave)
                                        navController.popBackStack()
                                    },
                                    onBack = { navController.popBackStack() }
                                )
                            }

                            composable("editGame/{gameId}") { backStackEntry ->
                                val gameId = backStackEntry.arguments?.getString("gameId")
                                val games by gameViewModel.games.collectAsState()
                                val gameToEdit = games.find { it.id == gameId }

                                gameToEdit?.let { game ->
                                    val isGameProtected = (game.protectionStatusId == 1)
                                    val isUserAdmin = (currentUserRole == "ADMIN")
                                    val canModify = !isGameProtected || isUserAdmin

                                    AddGameScreen(
                                        gameToEdit = game,
                                        canModify = canModify,
                                        onSave = { updatedGame ->
                                            val finalGame = updatedGame.copy(protectionStatusId = game.protectionStatusId)
                                            gameViewModel.updateGame(finalGame)
                                            navController.popBackTwoBackstacks()
                                        },
                                        onBack = { navController.popBackStack() }
                                    )
                                }
                            }

                            composable("detail/{gameId}") { backStackEntry ->
                                val gameId = backStackEntry.arguments?.getString("gameId")
                                val games by gameViewModel.games.collectAsState()
                                val selectedGame = games.find { it.id == gameId }

                                selectedGame?.let { game ->
                                    GameDetailScreen(
                                        game = game,
                                        currentUserRole = currentUserRole,
                                        onBack = { navController.popBackStack() },
                                        onEdit = { navController.navigate("editGame/${game.id}") },
                                        onDelete = {
                                            gameViewModel.deleteGame(game)
                                            navController.popBackStack()
                                        },
                                        onStatusChange = { updatedGame ->
                                            gameViewModel.updateGame(updatedGame)
                                        }
                                    )
                                }
                            }

                            composable("addProduct") {
                                AddProductScreen(
                                    onSave = { product ->
                                        storeViewModel.addProduct(product)
                                        navController.popBackStack()
                                    },
                                    onBack = { navController.popBackStack() }
                                )
                            }

                            composable("editProduct/{productId}") { backStackEntry ->
                                val productId = backStackEntry.arguments?.getString("productId")
                                val products by storeViewModel.products.collectAsState()
                                val productToEdit = products.find { it.id == productId }

                                productToEdit?.let { product ->
                                    AddProductScreen(
                                        productToEdit = product,
                                        onSave = { updatedProduct ->
                                            storeViewModel.updateProduct(updatedProduct)
                                            navController.popBackStack()
                                        },
                                        onBack = { navController.popBackStack() }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun NavController.popBackTwoBackstacks() {
        popBackStack()
        popBackStack()
    }
}
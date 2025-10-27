package com.example.bibliotecag_sdapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import bibliotecaG.data.local.GameDatabase
import bibliotecaG.data.local.GameDatabase.Companion.MIGRATION_1_2
import bibliotecaG.data.repository.GameRepository
import bibliotecaG.ui.screens.addGame.AddGameScreen
import bibliotecaG.ui.screens.detail.GameDetailScreen
import bibliotecaG.ui.screens.home.HomeScreen
import bibliotecaG.ui.screens.login.LoginScreen
import bibliotecaG.ui.theme.BibliotecaGTheme
import bibliotecaG.ui.viewmodel.AuthViewModel
import bibliotecaG.ui.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {

    // Inicialización de la base de datos
    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            GameDatabase::class.java,
            "game.db"
        )
            .addMigrations(MIGRATION_1_2) // <-- 2. Eliminamos la migración específica
            .fallbackToDestructiveMigration() // <-- 3. Añadimos esta línea
            .build()
    }

    // Inicialización del Repositorio
    private val gameRepository by lazy { GameRepository(db.gameDao()) }

    // Factory para crear el GameViewModel (ya que tiene dependencias)
    private val gameViewModelFactory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return GameViewModel(gameRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    // Inicialización de los ViewModels
    private val gameViewModel: GameViewModel by viewModels { gameViewModelFactory }
    private val authViewModel: AuthViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BibliotecaGTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    // Observamos el rol del usuario actual desde el AuthViewModel
                    val currentUserRole by authViewModel.currentUserRole.collectAsState()

                    NavHost(
                        navController = navController,
                        startDestination = "home"
                    ) {
                        // --- HomeScreen ---
                        composable("home") {
                            HomeScreen(
                                navController = navController,
                                gameViewModel = gameViewModel,
                                authViewModel = authViewModel
                            )
                        }

                        // --- LoginScreen ---
                        composable("login") {
                            LoginScreen(
                                authViewModel = authViewModel,
                                onLoginSuccess = {
                                    navController.popBackStack() // Vuelve a Home
                                },
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // --- AddGameScreen (Crear) ---
                        composable("addGame") {
                            AddGameScreen(
                                onSave = { game ->
                                    val gameWithRole = game.copy(creatorRole = currentUserRole ?: "USER")
                                    gameViewModel.addGame(gameWithRole)
                                    navController.popBackStack()
                                },
                                onBack = { navController.popBackStack() },
                                canModify = true
                            )
                        }

                        // --- AddGameScreen (Editar) ---
                        composable("editGame/{gameId}") { backStackEntry ->
                            val gameId = backStackEntry.arguments?.getString("gameId")
                            // Usamos collectAsState para seguridad de Compose
                            val games by gameViewModel.games.collectAsState()
                            val gameToEdit = games.find { it.id == gameId }

                            gameToEdit?.let { game ->
                                val isGameProtected = game.creatorRole == "ADMIN"
                                val isUserAdmin = currentUserRole == "ADMIN"
                                val canModify = !isGameProtected || isUserAdmin

                                AddGameScreen(
                                    gameToEdit = game,
                                    onSave = { updatedGame ->
                                        gameViewModel.updateGame(updatedGame)
                                        navController.popBackStack()
                                    },
                                    onBack = { navController.popBackStack() },
                                    canModify = canModify
                                )
                            }
                        }

                        // --- GameDetailScreen ---
                        composable("detail/{gameId}") { backStackEntry ->
                            val gameId = backStackEntry.arguments?.getString("gameId")
                            val games by gameViewModel.games.collectAsState()
                            val selectedGame = games.find { it.id == gameId }

                            selectedGame?.let { game ->
                                GameDetailScreen(
                                    game = game,
                                    onBack = { navController.popBackStack() },
                                    onEdit = { gameToEdit ->
                                        navController.navigate("editGame/${gameToEdit.id}")
                                    },
                                    onDelete = { gameToDelete ->
                                        gameViewModel.deleteGame(gameToDelete)
                                        navController.popBackStack()
                                    },
                                    currentUserRole = currentUserRole
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

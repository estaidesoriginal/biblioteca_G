package com.example.bibliotecag_sdapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import bibliotecaG.data.local.GameDatabase
import bibliotecaG.data.repository.GameRepository // <--- PASO 1: Importar Repositorio
import bibliotecaG.ui.viewmodel.GameViewModel
import bibliotecaG.ui.screens.home.HomeScreen
import bibliotecaG.ui.screens.addGame.AddGameScreen
import bibliotecaG.ui.screens.detail.GameDetailScreen
import bibliotecaG.ui.theme.BibliotecaGTheme // <--- PASO 2: Importar el Tema

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializamos Room (igual que antes)
        val db = Room.databaseBuilder(
            applicationContext,
            GameDatabase::class.java,
            "game.db"
        ).build()

        // --- CADENA DE INYECCIÓN DE DEPENDENCIAS ---
        // 1. Creamos el DAO
        val dao = db.gameDao()
        // 2. Creamos el Repositorio y le pasamos el DAO
        val repository = GameRepository(dao)
        // 3. Creamos el ViewModel y le pasamos el Repositorio
        val viewModel = GameViewModel(repository)
        // ------------------------------------------

        setContent {
            // ¡¡AQUÍ ESTÁ LA CORRECCIÓN!!
            // Envolvemos todo con el tema que creaste
            BibliotecaGTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()

                    // Tu NavHost (que ya no está en un archivo separado)
                    // usa el ViewModel que creamos arriba.
                    NavHost(
                        navController = navController,
                        startDestination = "home"
                    ) {
                        // --- HomeScreen ---
                        composable("home") {
                            HomeScreen(navController = navController, gameViewModel = viewModel)
                        }

                        // --- AddGameScreen para crear ---
                        composable("addGame") {
                            AddGameScreen(
                                onSave = { game ->
                                    viewModel.addGame(game)
                                    navController.popBackStack()
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }

                        // --- AddGameScreen para editar ---
                        composable("editGame/{gameId}") { backStackEntry ->
                            val gameId = backStackEntry.arguments?.getString("gameId")
                            // Usamos .value porque 'games' es un StateFlow
                            val gameToEdit = viewModel.games.value.find { it.id == gameId }

                            gameToEdit?.let { game ->
                                AddGameScreen(
                                    gameToEdit = game,
                                    onSave = { updatedGame ->
                                        viewModel.updateGame(updatedGame)
                                        navController.popBackStack()
                                    },
                                    onBack = { navController.popBackStack() }
                                )
                            }
                        }

                        // --- GameDetailScreen ---
                        composable("detail/{gameId}") { backStackEntry ->
                            val gameId = backStackEntry.arguments?.getString("gameId")
                            val selectedGame = viewModel.games.value.find { it.id == gameId }

                            selectedGame?.let { game ->
                                GameDetailScreen(
                                    game = game,
                                    onBack = { navController.popBackStack() },
                                    onEdit = { gameToEdit ->
                                        navController.navigate("editGame/${gameToEdit.id}")
                                    },
                                    onDelete = { gameToDelete ->
                                        viewModel.deleteGame(gameToDelete)
                                        navController.popBackStack()
                                    }
                                )
                            }
                        }
                    }
                }
            } // <- Fin de BibliotecaGTheme
        }
    }
}
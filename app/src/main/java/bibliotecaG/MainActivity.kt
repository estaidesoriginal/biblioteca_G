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
import bibliotecaG.ui.viewmodel.GameViewModel
import bibliotecaG.ui.screens.home.HomeScreen
import bibliotecaG.ui.screens.addGame.AddGameScreen
import bibliotecaG.ui.screens.detail.GameDetailScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializamos Room
        val db = Room.databaseBuilder(
            applicationContext,
            GameDatabase::class.java,
            "game.db"
        ).build()

        // Inicializamos ViewModel
        val viewModel = GameViewModel(db.gameDao())

        setContent {
            Surface(color = MaterialTheme.colorScheme.background) {
                val navController = rememberNavController()

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
        }
    }
}
package bibliotecaG.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import bibliotecaG.data.model.Game
import bibliotecaG.ui.screens.addGame.AddGameScreen
import bibliotecaG.ui.screens.detail.GameDetailScreen
import bibliotecaG.ui.screens.home.HomeScreen
import bibliotecaG.ui.viewmodel.GameViewModel

@Composable
fun NavGraph(navController: NavHostController, gameViewModel: GameViewModel) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        // --- HomeScreen ---
        composable("home") {
            HomeScreen(
                navController = navController,
                gameViewModel = gameViewModel
            )
        }

        // --- Agregar un juego nuevo ---
        composable("addGame") {
            AddGameScreen(
                gameToEdit = null,
                onSave = { newGame ->
                    gameViewModel.addGame(newGame)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }

        // --- GameDetailScreen ---
        composable("detail/{gameId}") { backStackEntry ->
            val gameId: String? = backStackEntry.arguments?.getString("gameId")
            val games: List<Game> by gameViewModel.games.collectAsState(initial = emptyList())
            val selectedGame: Game? = games.find { it.id == gameId }

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
                    }
                )
            }
        }

        // --- AddGameScreen para editar ---
        composable("editGame/{gameId}") { backStackEntry ->
            val gameId: String? = backStackEntry.arguments?.getString("gameId")
            val games: List<Game> by gameViewModel.games.collectAsState(initial = emptyList())
            val selectedGame: Game? = games.find { it.id == gameId }

            selectedGame?.let { game ->
                AddGameScreen(
                    gameToEdit = game,
                    onSave = { updatedGame ->
                        gameViewModel.updateGame(updatedGame)
                        navController.popBackStack() // vuelve al detalle
                    },
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

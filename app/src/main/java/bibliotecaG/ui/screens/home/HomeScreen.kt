package bibliotecaG.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import bibliotecaG.ui.components.GameCard
import bibliotecaG.ui.viewmodel.AuthViewModel
import bibliotecaG.ui.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    gameViewModel: GameViewModel,
    authViewModel: AuthViewModel
) {
    var searchQuery by remember { mutableStateOf("") }
    val games by gameViewModel.games.collectAsState()
    val shown = if (searchQuery.isBlank()) games else gameViewModel.search(searchQuery)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("La Biblioteca de Juegos") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("addGame") }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar")
            }
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar por título, descripción o etiqueta") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            if (shown.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay juegos. Agrega el primero con +", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(shown) { game ->
                        GameCard(game = game) { navController.navigate("detail/${game.id}") }
                    }
                }
            }
        }
    }
}



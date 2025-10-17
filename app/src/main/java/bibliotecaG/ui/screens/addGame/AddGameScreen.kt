package bibliotecaG.ui.screens.addGame

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.ImeAction
import java.util.UUID
import bibliotecaG.data.model.Game

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGameScreen(
    gameToEdit: Game? = null,       // Parámetro opcional para editar
    onSave: (Game) -> Unit,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf(gameToEdit?.title ?: "") }
    var description by remember { mutableStateOf(gameToEdit?.description ?: "") }
    var tags by remember { mutableStateOf(gameToEdit?.tags?.joinToString(", ") ?: "") }
    var imageUrl by remember { mutableStateOf(gameToEdit?.imageUrl ?: "") }
    var externalLinks by remember { mutableStateOf(gameToEdit?.externalLinks?.joinToString(", ") ?: "") }

    var showError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (gameToEdit == null) "Agregar Juego" else "Editar Juego",
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                if (title.isBlank()) {
                    showError = true
                } else {
                    val game = Game(
                        id = gameToEdit?.id ?: UUID.randomUUID().toString(),
                        title = title,
                        description = description,
                        tags = tags.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                        imageUrl = imageUrl.ifBlank { null },
                        externalLinks = externalLinks.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                    )
                    onSave(game)
                }
            }) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Check,
                    contentDescription = "Guardar"
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título") },
                isError = showError,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            if (showError) {
                Text(
                    text = "El título es obligatorio",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            OutlinedTextField(
                value = tags,
                onValueChange = { tags = it },
                label = { Text("Etiquetas (separadas por coma)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            OutlinedTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = { Text("URL de imagen") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Next
                )
            )

            OutlinedTextField(
                value = externalLinks,
                onValueChange = { externalLinks = it },
                label = { Text("Enlaces externos (separados por coma)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Done
                )
            )
        }
    }
}
package bibliotecaG.ui.screens.addGame

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import bibliotecaG.data.local.GameEntity
import bibliotecaG.data.model.Game
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGameScreen(
    gameToEdit: Game? = null,
    onSave: (Game) -> Unit,
    onBack: () -> Unit,
    canModify: Boolean = true
) {
    val isReadOnly = !canModify

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
                    val text = if (gameToEdit == null) {
                        "Agregar Juego"
                    } else if (isReadOnly) {
                        "Ver Juego"
                    } else {
                        "Editar Juego"
                    }
                    Text(
                        text = text,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (canModify) {
                FloatingActionButton(onClick = {
                    if (title.isBlank()) {
                        showError = true
                    } else {
                        val gameToSave: Game
                        if (gameToEdit == null) {
                            gameToSave = Game(

                                /** El 'id' se generará por defecto en el constructor de Game */

                                title = title,
                                description = description,
                                tags = tags.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                                imageUrl = imageUrl.ifBlank { null },
                                externalLinks = externalLinks.split(",").map { it.trim() }.filter { it.isNotEmpty() }

                            )
                        } else {
                            gameToSave = gameToEdit.copy(
                                title = title,
                                description = description,
                                tags = tags.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                                imageUrl = imageUrl.ifBlank { null },
                                externalLinks = externalLinks.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                            )
                        }
                        onSave(gameToSave)
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Guardar"
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título") },
                isError = showError,
                readOnly = isReadOnly,
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
                readOnly = isReadOnly,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            OutlinedTextField(
                value = tags,
                onValueChange = { tags = it },
                label = { Text("Etiquetas (separadas por coma)") },
                readOnly = isReadOnly,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            OutlinedTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = { Text("URL de imagen") },
                readOnly = isReadOnly,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Next
                )
            )

            OutlinedTextField(
                value = externalLinks,
                onValueChange = { externalLinks = it },
                label = { Text("Enlaces externos (separadas por coma)") },
                readOnly = isReadOnly,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Done
                )
            )
        }
    }
}
package bibliotecaG.ui.screens.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import bibliotecaG.data.model.Game
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun GameDetailScreen(
    game: Game,
    onBack: () -> Unit,
    onEdit: () -> Unit, // Ya no recibe Game, solo navega
    onDelete: (Game) -> Unit,
    // ¡NUEVO PARÁMETRO! Acción específica para cambiar el estado sin salir de la pantalla
    onStatusChange: (Game) -> Unit,
    currentUserRole: String?
) {
    val scrollState = rememberScrollState()
    val uriHandler = LocalUriHandler.current

    val isGameProtected = (game.protectionStatusId == 1)
    val isUserAdmin = (currentUserRole == "ADMIN")
    val canModify = !isGameProtected || isUserAdmin

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = game.title) },
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
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = onEdit, // Solo navega
                        enabled = canModify
                    ) {
                        Text("Editar")
                    }
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        onClick = { onDelete(game) },
                        enabled = canModify
                    ) {
                        Text("Eliminar")
                    }
                }

                if (isUserAdmin) {
                    Divider()
                    Button(
                        onClick = {
                            val newStatus = if (isGameProtected) 2 else 1
                            val updatedGame = game.copy(protectionStatusId = newStatus)

                            // ¡CORRECCIÓN! Usamos la nueva función dedicada
                            // Esto llamará directamente a la API en el ViewModel
                            onStatusChange(updatedGame)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isGameProtected) Color(0xFFFFA726) else Color(0xFF66BB6A)
                        )
                    ) {
                        Icon(
                            imageVector = if (isGameProtected) Icons.Default.LockOpen else Icons.Default.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isGameProtected) "Desproteger Juego (Hacer Público)" else "Proteger Juego (Solo Admin)")
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            game.imageUrl?.let { imageUrl ->
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = game.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .padding(bottom = 16.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Text(
                text = game.title,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            val protectionText = if (isGameProtected) "PROTEGIDO (Solo Admins)" else "PÚBLICO"
            val protectionColor = if (isGameProtected) Color(0xFFFFD700) else MaterialTheme.colorScheme.onSurfaceVariant

            Text(
                text = "Estado: $protectionText",
                style = MaterialTheme.typography.labelMedium,
                color = protectionColor,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = game.description,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (game.tags.isNotEmpty()) {
                Text(
                    text = "Etiquetas:",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    game.tags.forEach { tag ->
                        AssistChip(
                            onClick = {},
                            label = { Text(tag) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (game.externalLinks.isNotEmpty()) {
                Text(
                    text = "Enlaces relacionados:",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                game.externalLinks.forEach { link ->
                    TextButton(onClick = { uriHandler.openUri(link) }) {
                        Text(text = link)
                    }
                }
            }
        }
    }
}
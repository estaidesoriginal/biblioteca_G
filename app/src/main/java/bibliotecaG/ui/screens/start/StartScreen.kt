package bibliotecaG.ui.screens.start

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartScreen() {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Bienvenido a BibliotecaG") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Tarjeta de Bienvenida
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "¿Qué puedes hacer aquí?",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Sientete libre de compartir con todos en la App tu juego favorito o pasate por la tienda en busca de algo brillante.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Sección 1: Biblioteca
            InfoSection(
                title = "📚 Biblioteca de Juegos",
                description = "Gestiona la colección de juegos registrados en la app y comparte tu favoritos con todos. Puedes crear nuevos juegos y editar los existentes.\n\n⚠️ Nota importante: Los juegos marcados como 'Protegidos' no pueden ser editados ni eliminados por usuarios estándar."
            )

            // Sección 2: Tienda
            InfoSection(
                title = "🛍️ Tienda E-commerce",
                description = "Explora nuestro catálogo de productos relacionados con el gaming. Agrega items a tu carrito y realiza la compra de los productos que mas te gusten."
            )

            // Sección 3: Usuario
            InfoSection(
                title = "👤 Perfil y Personalización",
                description = "Inicia sesión para acceder a todas las funciones. Además, puedes personalizar la apariencia de la aplicación eligiendo entre diferentes temas de colores (Clásico, Natura, Cyber) desde tu perfil."
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "¡Empieza navegando con la barra inferior!",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun InfoSection(title: String, description: String) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
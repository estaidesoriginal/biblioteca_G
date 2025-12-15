package bibliotecaG.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import bibliotecaG.ui.theme.ThemeType
import bibliotecaG.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    currentTheme: ThemeType,
    onThemeChange: (ThemeType) -> Unit,
    onLogout: () -> Unit,
    onLoginRequest: () -> Unit
) {
    val currentUser by authViewModel.currentUser.collectAsState()

    val isLoggedIn = currentUser != null
    val displayName = currentUser?.name ?: "Invitado"
    val displayEmail = currentUser?.email ?: "No has iniciado sesión"
    val displayRole = currentUser?.role ?: "Visitante"

    Scaffold(
        topBar = { TopAppBar(title = { Text("Mi Perfil") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(
                        if (isLoggedIn) MaterialTheme.colorScheme.primaryContainer
                        else Color.Gray.copy(alpha = 0.3f)
                    )
                    .padding(16.dp),
                tint = if (isLoggedIn) MaterialTheme.colorScheme.onPrimaryContainer else Color.Gray
            )

            Spacer(Modifier.height(24.dp))

            if (isLoggedIn) {
                Text(
                    text = "Rol: $displayRole",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(16.dp))
            }

            OutlinedTextField(
                value = displayName,
                onValueChange = { },
                label = { Text("Nombre") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = displayEmail,
                onValueChange = { },
                label = { Text("Email") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            Text("Personalizar Apariencia", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ThemeOptionButton("Clásico", ThemeType.DEFAULT, currentTheme, onThemeChange)
                ThemeOptionButton("Natura", ThemeType.NATURE, currentTheme, onThemeChange)
                ThemeOptionButton("Cyber", ThemeType.CYBER, currentTheme, onThemeChange)
            }

            Spacer(Modifier.weight(1f))

            if (isLoggedIn) {
                Button(
                    onClick = onLogout,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cerrar Sesión")
                }
            } else {
                Button(
                    onClick = onLoginRequest,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Iniciar Sesión / Registrarse")
                }
            }
        }
    }
}

@Composable
fun ThemeOptionButton(
    text: String,
    theme: ThemeType,
    currentTheme: ThemeType,
    onClick: (ThemeType) -> Unit
) {
    FilterChip(
        selected = theme == currentTheme,
        onClick = { onClick(theme) },
        label = { Text(text) }
    )

}

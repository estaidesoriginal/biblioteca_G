package bibliotecaG.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

private val DarkBackground = Color(0xFF1A1A2E) // Un azul casi negro
private val DarkSurface = Color(0xFF1F2833)    // Un gris azulado oscuro
private val NeonPink = Color(0xFFFF00FF)      // Rosa neón brillante
private val LightText = Color(0xFFE0E0E0)      // Texto claro (casi blanco)
private val PrimaryPurple = Color(0xFF9370DB) // Morado medio
private val DarkText = Color(0xFF333333)      // Texto oscuro para modo claro

// Paleta de día (Ejemplo)
private val LightBackground = Color(0xFFF5F7FA)
private val WhiteSurface = Color(0xFFFFFFFF)
private val PrimaryBlue = Color(0xFF007BFF)

private val DarkColors = darkColorScheme(
    primary = NeonPink,
    onPrimary = Color.Black,
    secondary = PrimaryPurple,
    onSecondary = Color.White,
    background = DarkBackground,
    onBackground = LightText,
    surface = DarkSurface,
    onSurface = LightText,
    error = Color(0xFFCF6679)
)

private val LightColors = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    secondary = PrimaryPurple,
    onSecondary = Color.White,
    background = LightBackground,
    onBackground = DarkText,
    surface = WhiteSurface,
    onSurface = DarkText,
    error = Color(0xFFB00020)
)


@Composable
fun BibliotecaGTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = androidx.compose.material3.Typography(),
        content = content
    )
}
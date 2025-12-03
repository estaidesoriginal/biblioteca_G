package bibliotecaG.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// --- DEFINICIÓN DE COMBOS DE COLOR ---

// 1. Default (Azul)
private val DefaultColors = lightColorScheme(
    primary = Color(0xFF2196F3),
    onPrimary = Color.White,
    secondary = Color(0xFF03DAC6),
    background = Color(0xFFFAFAFA),
    surface = Color.White
)

// 2. Nature (Verde/Tierra)
private val NatureColors = lightColorScheme(
    primary = Color(0xFF4CAF50),
    onPrimary = Color.White,
    secondary = Color(0xFF8D6E63),
    background = Color(0xFFF1F8E9),
    surface = Color(0xFFDCEDC8)
)

// 3. Cyber (Oscuro/Neón)
private val CyberColors = lightColorScheme(
    primary = Color(0xFFFF00FF), // Magenta Neón
    onPrimary = Color(0xFF121212),
    secondary = Color(0xFF2FFF00), // Cyan
    background = Color(0x90121212), // Negro fondo
    surface = Color(0xFF1E1E1E), // Gris oscuro cards
    onSurface = Color(0xFF00F2FF)
)

@Composable
fun BibliotecaGTheme(
    themeType: ThemeType = ThemeType.DEFAULT, // Recibe el tipo de tema
    content: @Composable () -> Unit
) {
    // Seleccionamos la paleta según el enum
    val colors = when (themeType) {
        ThemeType.DEFAULT -> DefaultColors
        ThemeType.NATURE -> NatureColors
        ThemeType.CYBER -> CyberColors
    }

    MaterialTheme(
        colorScheme = colors,
        typography = androidx.compose.material3.Typography(),
        content = content
    )
}
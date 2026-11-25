package lorry.lebonfilm.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFF5EFE1),
    secondary = Color(0xFFEAC4D5),
    tertiary = Color(0xFFAA1C41)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFF5EFE1),
    secondary = Color(0xFFEAC4D5),
    tertiary = Color(0xFFAA1C41),

    // Other default colors to override
    background = Color(0xFFF5EFE1),
    surface = Color(0xFFEAC4D5),
    onPrimary = Color.Black,
    onSecondary = Color(0xFFEAC4D5),
    onTertiary = Color(0xFFEAC4D5),
    onBackground = Color.Black,
    onSurface = Color(0xFF1C1B1F),

)

@Composable
fun LeBonFilmTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
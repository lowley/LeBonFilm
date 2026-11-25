package lorry.lebonfilm.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import lorry.lebonfilm.MainActivity
import lorry.lebonfilm.hideKeyboard
import org.koin.java.KoinJavaComponent

class Screens(
    val label: String,
    val icon: ImageVector,
    var content: @Composable (MainActivity.(PaddingValues) -> Unit)? = null
) {

    class NavigationState() {
        val initial: Screens = CHERCHER
        var currentScreen by mutableStateOf(initial)
    }

    companion object {
        val searchActor by KoinJavaComponent.inject<SearchActor>(SearchActor::class.java)
        val tools by KoinJavaComponent.inject<Tools>(Tools::class.java)

        val CHERCHER = Screens(
            "Recherche", Icons.Default.LocationSearching,
            { padding: PaddingValues ->
                searchActor.SearchActorScreen(padding, this::hideKeyboard)
            })

        val ACTEUR = Screens(
            "Acteur", Icons.Default.AccountCircle,
            { padding: PaddingValues ->
                FilmsScreen((padding))
            });

        val TOOLS = Screens(
            "Réglages", Icons.Default.CellTower,
            { padding: PaddingValues ->
                tools.ManageToken(padding)
            }
        )

        val listOfScreens = listOf(CHERCHER, ACTEUR, TOOLS)
    }
}
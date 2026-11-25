package lorry.lebonfilm.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import lorry.lebonfilm._components.displayActor.DisplayedActor
import lorry.lebonfilm.ui.screens.Screens.NavigationState
import lorry.lebonfilm._repos.ActorManager
import lorry.lebonfilm._repos.ActorResponse
import lorry.lebonfilm.activityScope
import lorry.lebonfilm.colors
import org.koin.java.KoinJavaComponent.inject
import kotlin.getValue


class SearchActor {

    var searchedActor: MutableState<String> = mutableStateOf("")

    @Composable
    fun SearchActorScreen(
        padding: PaddingValues,
        hideKeyboard: () -> Unit
    ) {
        searchedActor = rememberSaveable { mutableStateOf("") }
        var candidateActors: MutableState<ActorResponse?> =
            rememberSaveable { mutableStateOf(null) }

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 10.dp)
        ) {
            SearchActor(searchedActor, candidateActors, hideKeyboard)
            Spacer(modifier = Modifier.height(20.dp))
            DisplayCandidates(candidateActors)
        }
    }

    @Composable
    fun SearchActor(
        searchedActor: MutableState<String>,
        candidateActors: MutableState<ActorResponse?>,
        hideKeyboard: () -> Unit
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(colors.secondary)
                .padding(vertical = 10.dp, horizontal = 10.dp)
        ) {
            Text(
                text = "Rechercher un acteur",
                fontWeight = Bold,
                modifier = Modifier
            )

            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = CenterVertically
            ) {
                TextField(
                    value = searchedActor.value,
                    onValueChange = { searchedActor.value = it },
                    //label = { Text("Acteur") },
                    placeholder = @Composable { Text("Nom") },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = colors.tertiary,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier.padding(top = 5.dp)
                )

                SearchButton(searchedActor, candidateActors)
                ClearButton(searchedActor, candidateActors)
            }
        }
    }

    @Composable
    private fun SearchButton(
        searchedActor: MutableState<String>,
        candidateActors: MutableState<ActorResponse?>
    ) {
        val actorManager: ActorManager by inject(ActorManager::class.java)

        Button(
            modifier = Modifier.padding(start = 5.dp),
            onClick = {
                if (searchedActor.value.isEmpty())
                    return@Button

                candidateActors.value = null
                activityScope.launch {
                    candidateActors.value = actorManager.search(searchedActor.value)
                }
            }
        ) {
            Text(
                modifier = Modifier,
                text = "Chercher"
            )
        }
    }

    @Composable
    private fun ClearButton(
        searchedActor: MutableState<String>,
        candidateActors: MutableState<ActorResponse?>
    ) {
        Button(
            modifier = Modifier.padding(start = 5.dp),
            onClick = {
                if (searchedActor.value.isEmpty())
                    return@Button

                candidateActors.value = null
                searchedActor.value = ""
            }
        ) {
            Text(
                modifier = Modifier.align(CenterVertically),
                textAlign = TextAlign.Center,
                text = "x"
            )
        }
    }

    @Composable
    fun DisplayCandidates(
        candidateActors: MutableState<ActorResponse?>
    ) {
        val displayedActor: DisplayedActor by inject(DisplayedActor::class.java)
        val navigationState: NavigationState by inject(NavigationState::class.java)

        if (candidateActors.value != null) {
            Text(
                text = "Candidats",
                fontWeight = Bold,
                modifier = Modifier
            )

            Spacer(modifier = Modifier.height(5.dp))
        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
        ) {

            candidateActors.value?.results
                ?.take(10)
                ?.forEach { candidate ->
                    Card(
                        modifier = Modifier,
                        onClick = {
                            displayedActor.actor = candidate
                            navigationState.currentScreen = Screens.listOfScreens
                                .first { it.label == "Acteur" }
                        }
                    ) {
                        Text(
                            modifier = Modifier.padding(bottom = 0.dp),
                            text = candidate.name,
                            fontSize = 16.sp,
                            fontWeight = Bold
                        )

                        Column() {
                            candidate.known_for_department?.let { dep ->
                                Text(
                                    modifier = Modifier.padding(top = 0.dp, bottom = 0.dp),
                                    text = dep,
                                    fontSize = 10.sp
                                )
                            }

                            candidate.popularity?.let { popularity ->
                                Text(
                                    modifier = Modifier.padding(top = 0.dp),
                                    text = "$popularity",
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
        }
    }

    @Composable
    fun Card(
        modifier: Modifier = Modifier,
        onClick: () -> Unit = {},
        content: @Composable ColumnScope.() -> Unit,
    ) {
        Column(
            modifier
                .clip(RoundedCornerShape(8.dp))
                .background(colors.surface)
                .padding(5.dp)
                .clickable {
                    onClick()
                }
        ) {
            content()
        }
    }
}
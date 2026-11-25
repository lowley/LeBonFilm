package lorry.lebonfilm.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle.Companion.Italic
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import lorry.lebonfilm.MainActivity
import lorry.lebonfilm._components.displayActor.DisplayedActor
import lorry.lebonfilm._repos.ActorFilmsManager
import lorry.lebonfilm._repos.ActorFilmsResponse
import lorry.lebonfilm._repos.CommonResponseBuilders
import lorry.lebonfilm.colors
import org.koin.android.ext.android.inject
import org.koin.java.KoinJavaComponent.inject

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

enum class SortType { ALPHABETICAL, POPULARITY }

fun formatDateInFrench(dateStr: String?): String {
    if (dateStr.isNullOrBlank()) return ""
    return try {
        val date = LocalDate.parse(dateStr)
        val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.FRENCH)
        date.format(formatter)
    } catch (_: Exception) {
        dateStr
    }
}

@Composable
fun MainActivity.FilmsScreen(
    padding: PaddingValues
) {

    val actorFilmsManager: ActorFilmsManager by inject(ActorFilmsManager::class.java)
    val movieCount = remember { actorFilmsManager.movieCount }
    val count = movieCount.value

    val movies by produceState<ActorFilmsResponse?>(
        initialValue = null,
        key1 = displayedActor.actor?.id
    ) {
        val movies = actorFilmsManager.search(displayedActor.actor?.id)
        value = movies
    }

    var sortType by rememberSaveable { mutableStateOf(SortType.POPULARITY) }


    Column(
        modifier = Modifier
            .padding(padding)
            .padding(horizontal = 10.dp)
            .fillMaxWidth()
    ) {
        if (displayedActor.actor == null) {
            Text(
                text = "Aucun acteur selectionné",
                fontWeight = Bold,
                modifier = Modifier
            )
        } else {

            Text(
                text = "${displayedActor.actor?.name}",
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                textAlign = TextAlign.Center,
                fontSize = 25.sp
            )


//            Text(
//                text = "${if (displayedActor.actor?.gender == 1) "Actrice" else "Acteur"}: ${displayedActor.actor?.name}",
//                fontWeight = Bold,
//                modifier = Modifier
//            )
        }

        if (displayedActor.actor == null && movies != null) {
            Text(
                text = "Aucun acteur selectionné",
                fontWeight = Bold,
                modifier = Modifier
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = if (count != null) "$count films trouvés" else {
                    if (movies != null) "Il n'y a aucun film trouvé" else ""
                },
                fontWeight = FontWeight.Normal,
                modifier = Modifier
                    .align(Alignment.CenterStart)
            )

            IconButton(
                modifier = Modifier
                    .align(Alignment.CenterEnd),
                onClick = {
                    if (sortType == SortType.POPULARITY) sortType = SortType.ALPHABETICAL
                    else sortType = SortType.POPULARITY
                },

                ) {
                Icon(
                    imageVector = if (sortType == SortType.POPULARITY) Icons.Default.AccountBalance
                    else Icons.Default.CalendarToday,
                    contentDescription = "Options"
                )
            }
        }


        if (movies != null && count != null && count > 0) {
            MovieZone(
                movies,
                sortType
            )
        }
    }
}

@Composable
fun MainActivity.MovieZone(
    movies: ActorFilmsResponse?,
    sortType: SortType
) {

    val scrollState = rememberScrollState()

    val rows = 1
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
    ) {

        FlowRow(
            modifier = Modifier
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            maxItemsInEachRow = rows
        ) {
            val itemModifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(colors.secondary)

            movies?.results
                ?.sortedWith { movie0, movie1 ->
                    if (sortType == SortType.POPULARITY)
                        -(movie0.popularity?.compareTo(movie1?.popularity ?: 0.0) ?: 0)
                    else
                        movie0.release_date?.compareTo(movie1?.release_date ?: "") ?: 0
                }
                ?.forEach { movie ->
                    MovieCard(
                        modifier = itemModifier,
                        movie = movie,
                        sortType = sortType
                    )
                }
        }
    }
}

@Composable
fun MovieCard(
    modifier: Modifier = Modifier,
    movie: CommonResponseBuilders.Movie,
    sortType: SortType,
) {
    Card(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 60.dp)
                        .align(Alignment.CenterStart),
                    text = movie.title ?: "",
                    fontWeight = Bold,
                    textAlign = TextAlign.Center
                )

                Text(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    text = if (sortType == SortType.POPULARITY) "${movie.popularity}" else formatDateInFrench(
                        movie.release_date
                    ),
                    fontSize = 10.sp
                )
            }


            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                AsyncImage(
                    model = "https://image.tmdb.org/t/p/w400" + movie.backdrop_path,
                    contentDescription = "image du film",
                    modifier = Modifier.width(120.dp)
                )

                CardRight(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 5.dp),
                    movie = movie
                )
            }
        }
    }
}

@Composable
fun CardRight(
    modifier: Modifier = Modifier,
    movie: CommonResponseBuilders.Movie
) {

    val searchActor by inject<SearchActor>(SearchActor::class.java)
    val displayedActor: DisplayedActor by inject(DisplayedActor::class.java)

    Column(
        modifier = modifier
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Genre: ${movie.genre_names?.joinToString(", ")}"
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Résumé: ${movie.overview}",
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(5.dp))

        if (!movie.actors.isNullOrEmpty()) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Acteurs:"
            )

            val scrollState = rememberScrollState()
            val density = LocalDensity.current
            var textHeightDp by remember { mutableStateOf(150.dp) }

            val blockParentScrollConnection = remember {
                object : NestedScrollConnection {
                    override fun onPostScroll(
                        consumed: Offset,
                        available: Offset,
                        source: NestedScrollSource
                    ): Offset {
                        // Consomme tout le défilement restant afin d'empêcher le scroll de la page parente
                        return available
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Colonne de gauche : reçoit le défilement tactile sans propager le scroll à la page parente
                Box(
                    modifier = Modifier
                        .width(28.dp)
                        .height(150.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(colors.surface)
                        .nestedScroll(blockParentScrollConnection)
                        .verticalScroll(scrollState, enabled = true)
                ) {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(textHeightDp)
                            .border(1.dp, colors.tertiary)
                    )
                }

                // Colonne de droite : affiche le texte et suit le scrollState, mais désactivée au toucher (enabled = false)
                Column(
                    modifier = Modifier
                        .height(150.dp)
                        .weight(1f)
                        .verticalScroll(scrollState, enabled = false)
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coord ->
                                textHeightDp = with(density) { coord.size.height.toDp() }
                            },
                        text = buildAnnotatedString {
                            val total = movie.actors.size
                            movie.actors.forEachIndexed { index, actor ->
                                withLink(
                                    LinkAnnotation.Clickable(
                                        tag = "acteur",
                                        styles = TextLinkStyles(
                                            style = SpanStyle(
                                                color = Color.Black,
                                                textDecoration = TextDecoration.Underline,
                                                fontStyle = Italic
                                            )
                                        ),
                                        linkInteractionListener = {
                                            searchActor.searchedActor.value = actor.name ?: ""
                                            displayedActor.actor = CommonResponseBuilders.Actor(
                                                id = actor.id,
                                                name = actor.name ?: "",
                                                profile_path = actor.profile_path,
                                                gender = null,
                                            )
                                        }
                                    )
                                ) {
                                    append(actor.name)
                                }

                                actor.character?.let { character ->
                                    append(" (")
                                    append(character)
                                    append(")")
                                }

                                if (index != total - 1)
                                    append(", ")
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}


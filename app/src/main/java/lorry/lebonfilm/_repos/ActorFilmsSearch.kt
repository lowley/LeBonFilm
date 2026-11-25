package lorry.lebonfilm._repos

import androidx.compose.runtime.mutableStateOf
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.Serializable
import org.koin.java.KoinJavaComponent.inject

class ActorFilmsManager {

    val repo: APIRepo by inject(APIRepo::class.java)
    val movieCount = mutableStateOf<Int?>(null)

    suspend fun search(searchedActorId: Int?): ActorFilmsResponse? {

        if (searchedActorId == null)
            return null

        return try {
            //1. page 1
            val response: HttpResponse =
                repo.client.get("https://api.themoviedb.org/3/discover/movie") {
                    url {
                        parameters.append("with_cast", searchedActorId.toString())
                        parameters.append("language", "fr-FR")
                    }
                }

            var parsed: ActorFilmsResponse? = response.body()
            movieCount.value = parsed?.total_results

            //2. autres pages en parallèle
            if (parsed?.total_pages != null && parsed.total_pages > 1) {
                val pages = (2..parsed.total_pages).toList()
                val additionalPagesResults = coroutineScope {
                    pages.map { pageIndex ->
                        async {
                            try {
                                val internalResponse: HttpResponse =
                                    repo.client.get("https://api.themoviedb.org/3/discover/movie") {
                                        url {
                                            parameters.append("with_cast", searchedActorId.toString())
                                            parameters.append("language", "fr-FR")
                                            parameters.append("page", pageIndex.toString())
                                        }
                                    }
                                val internalParsed: ActorFilmsResponse = internalResponse.body()
                                internalParsed.results ?: emptyList()
                            } catch (_: Exception) {
                                emptyList()
                            }
                        }
                    }.awaitAll()
                }

                val allMovies = (parsed.results ?: emptyList()) + additionalPagesResults.flatten()
                parsed = parsed.copy(results = allMovies)
            }

            //3 & 4. genres et acteurs de chaque film en parallèle
            val movies = parsed?.results ?: emptyList()
            val moviesWithGenresAndCasts = coroutineScope {
                movies.map { movie ->
                    async {
                        val movieId = movie.id ?: return@async movie

                        val genresDeferred = async {
                            try {
                                val genresResponse: HttpResponse =
                                    repo.client.get("https://api.themoviedb.org/3/movie/$movieId") {
                                        url {
                                            parameters.append("language", "fr-FR")
                                        }
                                    }
                                val genres = genresResponse.body<GenresResponse>()
                                genres.genres.map { it.name }
                            } catch (_: Exception) {
                                emptyList()
                            }
                        }

                        val castsDeferred = async {
                            try {
                                val castsResponse: HttpResponse =
                                    repo.client.get("https://api.themoviedb.org/3/movie/$movieId/credits") {
                                        url {
                                            parameters.append("language", "fr-FR")
                                        }
                                    }
                                val casts = castsResponse.body<CastsResponse>()
                                casts.cast
                            } catch (_: Exception) {
                                emptyList()
                            }
                        }

                        val genreNames = genresDeferred.await()
                        val actorNames = castsDeferred.await()

                        movie.copy(
                            genre_names = genreNames,
                            actors = actorNames
                        )
                    }
                }.awaitAll()
            }

            parsed?.copy(results = moviesWithGenresAndCasts)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

@Serializable
data class ActorFilmsResponse(
    val page: Int? = null,
    val results: List<CommonResponseBuilders.Movie>? = null,
    val total_pages: Int? = null,
    val total_results: Int? = null
)

@Serializable
data class GenresResponse(
    val genres: List<Genre> = emptyList()
)

@Serializable
data class Genre(
    val name: String = ""
)

@Serializable
data class CastsResponse(
    val cast: List<Cast> = emptyList()
)

@Serializable
data class Cast(
    val adult: Boolean? = null,
    val id: Int? = null,
    val known_for_department: String? = null,
    val name: String? = null,
    val profile_path: String? = null,
    val character: String? = null
)

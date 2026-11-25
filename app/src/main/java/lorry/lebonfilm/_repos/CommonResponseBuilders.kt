package lorry.lebonfilm._repos

import kotlinx.serialization.Serializable

object CommonResponseBuilders {

    @Serializable
    data class Actor(
        val adult: Boolean? = null,
        val gender: Int? = null,
        val id: Int? = null,
        val known_for_department: String? = null,
        val name: String = "",
        val original_name: String? = null,
        val popularity: Double? = null,
        val profile_path: String? = null,
        val known_for: List<Movie>? = null
    )

    @Serializable
    data class Movie(
        val adult: Boolean? = null,
        val backdrop_path: String? = null,
        val id: Int? = null,
        val title: String? = null,
        val original_title: String? = null,
        val overview: String? = null,
        val poster_path: String? = null,
        val media_type: String? = null,
        val genre_names: List<String>? = null,
        val actors: List<Cast>? = null,
        val original_language: String? = null,
        val popularity: Double? = null,
        val release_date: String? = null,
        val softcore: Boolean? = null,
        val video: Boolean? = null,
        val vote_average: Double? = null,
        val vote_count: Int? = null
    )
}

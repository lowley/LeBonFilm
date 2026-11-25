package lorry.lebonfilm._repos

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import kotlinx.serialization.Serializable
import org.koin.java.KoinJavaComponent.inject

class ActorManager {

    val repo: APIRepo by inject(APIRepo::class.java)

    suspend fun search(searchedActor: String): ActorResponse? {
        if (searchedActor.isEmpty())
            return null

        return try {
            val response: HttpResponse =
                repo.client.get("https://api.themoviedb.org/3/search/person") {
                    url {
                        parameters.append("query", searchedActor)
                        parameters.append("language", "fr-FR")
                    }
                }
            response.body<ActorResponse>()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

@Serializable
data class ActorResponse(
    val page: Int? = null,
    val results: List<CommonResponseBuilders.Actor>? = null,
    val total_pages: Int? = null,
    val total_results: Int? = null
)

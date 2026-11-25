package lorry.lebonfilm._repos

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import lorry.lebonfilm.ui.screens.Tools

class APIRepo(private val tools: Tools) {

    val client = HttpClient(CIO) {
        install(Auth) {
            bearer {
                loadTokens {
                    // Load tokens from a local storage and return them as the 'BearerTokens' instance
                    val activeToken = tools.token.value?.trim()?.takeIf { it.isNotBlank() } ?: ""
                    BearerTokens(activeToken, refreshToken = null)
                }
            }
        }
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = false
                ignoreUnknownKeys = true
            })
        }
    }
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
}

package lorry.lebonfilm.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.io.File

class Tools(private val context: Context) {

    val defaultToken = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI2OWY3Y2QxZTIyNjJhZmM0MzE2MDdkMDBiOWExMmExMSIsIm5iZiI6MTc5NDY1MDEzNy45NjMsInN1YiI6IjZhZjgzMDE5YWU3ZDk4MTRhMDI2ZTY5MiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.M3AevHaHtVQBIyaFuo4Dl8xpOqHjZjkgW6po-flAwKU"

    private val tokenFile: File
        get() = File(context.filesDir, "token.txt")

    var token = mutableStateOf<String?>(null)

    init {
        loadToken()
    }

    fun loadToken() {
        try {
            if (tokenFile.exists()) {
                val content = tokenFile.readText().trim()
                token.value = if (content.isNotEmpty()) content else defaultToken
            } else {
                token.value = defaultToken
            }
        } catch (e: Exception) {
            e.printStackTrace()
            token.value = defaultToken
        }
    }

    fun saveToken(value: String?) {
        try {
            token.value = value
            if (value.isNullOrBlank()) {
                if (tokenFile.exists()) {
                    tokenFile.delete()
                }
            } else {
                tokenFile.writeText(value.trim())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Composable
    fun ManageToken(padding: PaddingValues = PaddingValues(0.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Token : ",
                    modifier = Modifier.padding(end = 8.dp)
                )

                OutlinedTextField(
                    value = token.value ?: "",
                    onValueChange = { token.value = it },
                    label = { Text("Valeur hexa") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        saveToken(token.value)
                    }
                ) {
                    Text("Enregistrer")
                }

                Spacer(Modifier.width(8.dp))

                Button(
                    onClick = {
                        loadToken()
                    }
                ) {
                    Text("Recharger")
                }
            }
        }
    }
}

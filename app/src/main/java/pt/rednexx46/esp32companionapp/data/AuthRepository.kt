package pt.rednexx46.esp32companionapp.data

import android.content.Context
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import pt.rednexx46.esp32companionapp.SessionManager

class AuthRepository {

    suspend fun login(context: Context, username: String, password: String): Boolean {
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json()
            }
        }

        return try {
            android.util.Log.d("AuthRepository", "Attempting login for user: $username")
            val response = client.post("${SessionManager.getApiUrl(context)}/api/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("username" to username, "password" to password))
            }
            android.util.Log.d("AuthRepository", "Received response: ${response.status}")

            val body: JsonObject = response.body()
            android.util.Log.d("AuthRepository", "Response body: $body")
            val token = body["token"]?.jsonPrimitive?.content
            if (token != null) {
                android.util.Log.d("AuthRepository", "Token received and saved")
                SessionManager.saveToken(context, token)
                true
            } else {
                android.util.Log.w("AuthRepository", "No token found in response")
                false
            }

        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "Login failed", e)
            false
        } finally {
            client.close()
        }
    }
}
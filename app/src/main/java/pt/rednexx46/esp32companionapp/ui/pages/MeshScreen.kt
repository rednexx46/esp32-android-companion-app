package pt.rednexx46.esp32companionapp.ui.pages

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Hub
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.SignalCellularAlt
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import pt.rednexx46.esp32companionapp.SessionManager
import java.time.Instant
import java.time.format.DateTimeFormatter

@Serializable
data class Node(
    val last_seen: Long,
    val mac: String,
    val rssi: Int? = null
)

@Serializable
data class StatusRecord(
    val _id: String,
    val gateway_id: String,
    val nodes: List<Node> = emptyList(),
    val timestamp: String
)

@Serializable
data class StatusResponse(
    val page: Int,
    val pageSize: Int,
    val records: List<StatusRecord>,
    val totalCount: Int,
    val totalPages: Int
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MeshScreen() {
    val context = LocalContext.current
    var page by remember { mutableStateOf(1) }
    var totalPages by remember { mutableStateOf(1) }
    var loading by remember { mutableStateOf(false) }
    val statusList = remember { mutableStateListOf<StatusRecord>() }
    val scope = rememberCoroutineScope()

    fun loadStatus() {
        if (loading || page > totalPages) return
        loading = true
        scope.launch {
            try {
                val token = SessionManager.getToken(context) ?: return@launch
                val apiUrl = SessionManager.getApiUrl(context) ?: return@launch

                val client = HttpClient(CIO) {
                    install(ContentNegotiation) {
                        json(Json { ignoreUnknownKeys = true })
                    }
                }

                val response: StatusResponse = client.get("$apiUrl/api/status") {
                    parameter("page", page)
                    parameter("pageSize", 5)
                    header(HttpHeaders.Authorization, "Bearer $token")
                    accept(ContentType.Application.Json)
                }.body()

                statusList.addAll(response.records)
                totalPages = response.totalPages
                page++
                client.close()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                loading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        if (statusList.isEmpty()) loadStatus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            "Mesh Network Status",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(statusList) { status ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Hub, contentDescription = "Gateway", tint = Color(0xFF4CAF50))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = status.gateway_id,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Schedule, contentDescription = "Timestamp", tint = Color(0xFF607D8B))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                try {
                                    DateTimeFormatter.ISO_INSTANT.format(Instant.parse(status.timestamp))
                                } catch (e: Exception) {
                                    status.timestamp
                                },
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (status.nodes.isNotEmpty()) {
                            status.nodes.forEach { node ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Rounded.Memory, contentDescription = "MAC", tint = Color(0xFF3F51B5))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("MAC: ${node.mac}", style = MaterialTheme.typography.bodySmall)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Rounded.History, contentDescription = "Last seen", tint = Color(0xFF009688))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Last Seen: ${node.last_seen}", style = MaterialTheme.typography.bodySmall)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Rounded.SignalCellularAlt, contentDescription = "RSSI", tint = Color(0xFFFF5722))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("RSSI: ${node.rssi ?: "N/A"}", style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                }
                            }
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.Info, contentDescription = "No nodes", tint = Color.Gray)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("No nodes registered.", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }

        if (page <= totalPages) {
            Button(
                onClick = { loadStatus() },
                enabled = !loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Load More")
                }
            }
        }
    }
}
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
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Send
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
data class KPIRecord(
    val _id: String,
    val device_id: String,
    val failures: Int? = null,
    val readings: Int? = null,
    val sent: Int? = null,
    val timestamp: String,
    val uptime: Int
)

@Serializable
data class KPIResponse(
    val page: Int,
    val pageSize: Int,
    val records: List<KPIRecord>,
    val totalCount: Int,
    val totalPages: Int
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun KPIsScreen() {
    val context = LocalContext.current
    var page by remember { mutableStateOf(1) }
    var totalPages by remember { mutableStateOf(1) }
    var loading by remember { mutableStateOf(false) }
    val kpiList = remember { mutableStateListOf<KPIRecord>() }
    val scope = rememberCoroutineScope()

    fun loadKPIs() {
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

                val response: KPIResponse = client.get("$apiUrl/api/kpis") {
                    parameter("page", page)
                    parameter("pageSize", 5)
                    header(HttpHeaders.Authorization, "Bearer $token")
                    accept(ContentType.Application.Json)
                }.body()

                kpiList.addAll(response.records)
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
        if (kpiList.isEmpty()) loadKPIs()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text("Device KPIs", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(kpiList) { kpi ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Memory, contentDescription = "Device", tint = Color(0xFF2196F3))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = kpi.device_id,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Schedule, contentDescription = "Timestamp", tint = Color(0xFF607D8B))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                try {
                                    DateTimeFormatter.ISO_INSTANT.format(Instant.parse(kpi.timestamp))
                                } catch (e: Exception) {
                                    kpi.timestamp
                                },
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Bolt, contentDescription = "Uptime", tint = Color(0xFF9C27B0))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Uptime: ${kpi.uptime} sec", style = MaterialTheme.typography.bodySmall)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (kpi.readings != null && kpi.sent != null && kpi.failures != null) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.BarChart, contentDescription = "Readings", tint = Color(0xFF4CAF50))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Readings: ${kpi.readings}", style = MaterialTheme.typography.bodySmall)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Send, contentDescription = "Sent", tint = Color(0xFF03A9F4))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Sent: ${kpi.sent}", style = MaterialTheme.typography.bodySmall)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Close, contentDescription = "Failures", tint = Color(0xFFE53935))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Failures: ${kpi.failures}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }

        if (page <= totalPages) {
            Button(
                onClick = { loadKPIs() },
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
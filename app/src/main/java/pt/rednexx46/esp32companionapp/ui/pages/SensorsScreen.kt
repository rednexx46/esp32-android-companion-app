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
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Sensors
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Thermostat
import androidx.compose.material.icons.rounded.WaterDrop
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
import androidx.compose.ui.graphics.vector.ImageVector
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
data class SensorRecord(
    val _id: String,
    val device_id: String,
    val payload: String,
    val timestamp: String
)

@Serializable
data class SensorResponse(
    val page: Int,
    val pageSize: Int,
    val records: List<SensorRecord>,
    val totalCount: Int,
    val totalPages: Int
)

data class ParsedSensorData(
    val temperature: String? = null,
    val humidity: String? = null,
    val pressure: String? = null,
    val ldr: String? = null
)

fun parsePayloadToData(payload: String): ParsedSensorData {
    val regex = Regex("(T=([\\d.]+)C)?\\s*(H=([\\d.]+)%?)?\\s*(P=([\\d.]+)hPa)?\\s*(LDR=\\s*([\\d.]+))?")
    val match = regex.find(payload)
    val groups = match?.groupValues
    return ParsedSensorData(
        temperature = groups?.getOrNull(2)?.takeIf { it.isNotBlank() },
        humidity = groups?.getOrNull(4)?.takeIf { it.isNotBlank() },
        pressure = groups?.getOrNull(6)?.takeIf { it.isNotBlank() },
        ldr = groups?.getOrNull(8)?.takeIf { it.isNotBlank() }?.trim()
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SensorsScreen() {
    val context = LocalContext.current
    var page by remember { mutableStateOf(1) }
    var totalPages by remember { mutableStateOf(1) }
    var loading by remember { mutableStateOf(false) }
    val sensorList = remember { mutableStateListOf<SensorRecord>() }
    val scope = rememberCoroutineScope()

    fun loadData() {
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

                val response: SensorResponse = client.get("$apiUrl/api/data") {
                    parameter("page", page)
                    parameter("pageSize", 5)
                    header(HttpHeaders.Authorization, "Bearer $token")
                    accept(ContentType.Application.Json)
                }.body()

                sensorList.addAll(response.records)
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
        if (sensorList.isEmpty()) loadData()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            "Latest Sensor Readings",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(sensorList) { record ->
                val data = parsePayloadToData(record.payload)

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Sensors, contentDescription = "Device", tint = Color(0xFF2196F3))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = record.device_id,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.AccessTime, contentDescription = "Timestamp", tint = Color(0xFF607D8B))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                try {
                                    DateTimeFormatter.ISO_INSTANT.format(Instant.parse(record.timestamp))
                                } catch (e: Exception) {
                                    record.timestamp
                                },
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        data.temperature?.let {
                            SensorReadingRow(
                                icon = Icons.Rounded.Thermostat,
                                label = "Temperature",
                                value = "$it ºC",
                                color = Color(0xFFEF5350)
                            )
                        }

                        data.humidity?.let {
                            SensorReadingRow(
                                icon = Icons.Rounded.WaterDrop,
                                label = "Humidity",
                                value = "$it %",
                                color = Color(0xFF42A5F5)
                            )
                        }

                        data.pressure?.let {
                            SensorReadingRow(
                                icon = Icons.Rounded.Speed,
                                label = "Pressure",
                                value = "$it hPa",
                                color = Color(0xFFAB47BC)
                            )
                        }

                        data.ldr?.let {
                            SensorReadingRow(
                                icon = Icons.Rounded.LightMode,
                                label = "Light",
                                value = it,
                                color = Color(0xFFFFA726)
                            )
                        }
                    }
                }
            }
        }

        if (page <= totalPages) {
            Button(
                onClick = { loadData() },
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

@Composable
fun SensorReadingRow(icon: ImageVector, label: String, value: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(icon, contentDescription = label, tint = color)
        Spacer(modifier = Modifier.width(8.dp))
        Text("$label: $value", style = MaterialTheme.typography.bodyMedium)
    }
}
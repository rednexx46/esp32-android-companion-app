package pt.rednexx46.esp32companionapp.ui.components

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import pt.rednexx46.esp32companionapp.SessionManager

@Composable
fun SettingsDialog(
    context: Context,
    onDismiss: () -> Unit
) {
    var apiUrl by remember { mutableStateOf(SessionManager.getApiUrl(context) ?: "") }
    var mqttUrl by remember { mutableStateOf(SessionManager.getMqttUrl(context) ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                SessionManager.saveApiUrl(context, apiUrl)
                SessionManager.saveMqttUrl(context, mqttUrl)
                onDismiss()
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text("Settings") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = apiUrl,
                    onValueChange = { apiUrl = it },
                    label = { Text("API URL") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = mqttUrl,
                    onValueChange = { mqttUrl = it },
                    label = { Text("MQTT URL") },
                    singleLine = true
                )
            }
        },
        shape = RoundedCornerShape(12.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}
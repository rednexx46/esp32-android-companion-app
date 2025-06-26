package pt.rednexx46.esp32companionapp.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import pt.rednexx46.esp32companionapp.ui.components.SettingsDialog
import pt.rednexx46.esp32companionapp.ui.pages.*

enum class DashboardPage(val label: String, val icon: ImageVector) {
    Home("Home", Icons.Default.Home),
    Mesh("Mesh", Icons.Default.Share),
    Sensors("Sensors", Icons.Default.Info),
    KPIs("KPIs", Icons.AutoMirrored.Filled.List),
    Profile("Profile", Icons.Default.Person)
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardScreen(navController: NavController) {
    val context = LocalContext.current
    var currentPage by remember { mutableStateOf(DashboardPage.Home) }
    var showSettings by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                DashboardPage.entries.forEach { page ->
                    NavigationBarItem(
                        icon = { Icon(page.icon, contentDescription = page.label) },
                        label = { Text(page.label) },
                        selected = page == currentPage,
                        onClick = { currentPage = page }
                    )
                }
            }
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Main content based on page
                when (currentPage) {
                    DashboardPage.Home -> HomeScreen()
                    DashboardPage.Mesh -> MeshScreen()
                    DashboardPage.Sensors -> SensorsScreen()
                    DashboardPage.KPIs -> KPIsScreen()
                    DashboardPage.Profile -> ProfileScreen(navController = navController)
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(end = 16.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.large,
                        tonalElevation = 6.dp,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shadowElevation = 2.dp
                    ) {
                        IconButton(onClick = { showSettings = true }) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    )

    if (showSettings) {
        SettingsDialog(context = context, onDismiss = { showSettings = false })
    }
}
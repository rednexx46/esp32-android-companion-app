package pt.rednexx46.esp32companionapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pt.rednexx46.esp32companionapp.ui.DashboardScreen
import pt.rednexx46.esp32companionapp.ui.LoginScreen
import pt.rednexx46.esp32companionapp.ui.theme.ESP32CompanionAppTheme
import pt.rednexx46.esp32companionapp.viewmodel.LoginViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ESP32CompanionAppTheme {
                val navController = rememberNavController()
                val startDestination = if (SessionManager.isLoggedIn(this)) "dashboard" else "login"

                NavHost(navController = navController, startDestination = startDestination) {
                    composable("login") {
                        val viewModel = remember { LoginViewModel(this@MainActivity) }
                        LoginScreen(viewModel) {
                            navController.navigate("dashboard") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    }
                    composable("dashboard") {
                        DashboardScreen(navController)
                    }
                }
            }
        }
    }
}
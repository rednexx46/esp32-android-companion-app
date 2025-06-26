package pt.rednexx46.esp32companionapp.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.rednexx46.esp32companionapp.data.AuthRepository

class LoginViewModel(private val context: Context) : ViewModel() {

    private val authRepo = AuthRepository()

    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var loginError by mutableStateOf<String?>(null)

    fun login(onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            loginError = null

            val success = authRepo.login(context, username, password)
            if (success) {
                onSuccess()
            } else {
                loginError = "Login failed. Please check your credentials."
            }

            isLoading = false
        }
    }
}
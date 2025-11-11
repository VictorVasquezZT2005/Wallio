package com.example.wallio.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.wallio.data.model.User
import com.example.wallio.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthState(
    val isAuthenticated: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val user: User? = null
)

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        loadInitialAuthState()
    }

    private fun loadInitialAuthState() {
        val currentUser = authRepository.getCurrentUser()
        _authState.value = AuthState(
            isAuthenticated = authRepository.isUserLoggedIn(),
            user = currentUser
        )
        Log.d("AuthViewModel", "🔐 Estado inicial: ${if (currentUser != null) "Autenticado - ${currentUser.name}" else "No autenticado"}")
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)
            try {
                val result = authRepository.login(email, password)
                if (result.isSuccess) {
                    val user = result.getOrNull()
                    _authState.value = AuthState(
                        isAuthenticated = true,
                        user = user
                    )
                    Log.d("AuthViewModel", "✅ Login exitoso: ${user?.name}")
                } else {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Error desconocido"
                    )
                    Log.e("AuthViewModel", "❌ Error en login: ${result.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error durante el login"
                )
                Log.e("AuthViewModel", "❌ Excepción en login: ${e.message}")
            }
        }
    }

    fun register(email: String, password: String, name: String) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)
            try {
                val result = authRepository.register(email, password, name)
                if (result.isSuccess) {
                    val user = result.getOrNull()
                    _authState.value = AuthState(
                        isAuthenticated = true,
                        user = user
                    )
                    Log.d("AuthViewModel", "✅ Registro exitoso: ${user?.name}")
                } else {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Error desconocido"
                    )
                    Log.e("AuthViewModel", "❌ Error en registro: ${result.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error durante el registro"
                )
                Log.e("AuthViewModel", "❌ Excepción en registro: ${e.message}")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _authState.value = AuthState(isAuthenticated = false, user = null)
            Log.d("AuthViewModel", "🚪 Logout realizado")
        }
    }

    fun clearError() {
        _authState.value = _authState.value.copy(error = null)
    }

    fun refreshAuthState() {
        val currentUser = authRepository.getCurrentUser()
        _authState.value = AuthState(
            isAuthenticated = authRepository.isUserLoggedIn(),
            user = currentUser
        )
        Log.d("AuthViewModel", "🔄 Estado refrescado: ${if (currentUser != null) "Autenticado - ${currentUser.name}" else "No autenticado"}")
    }

    // Método para refrescar datos del usuario desde Firestore
    suspend fun refreshUserData() {
        try {
            val user = authRepository.refreshUserData()
            if (user != null) {
                _authState.value = AuthState(
                    isAuthenticated = true,
                    user = user
                )
                Log.d("AuthViewModel", "✅ Datos de usuario refrescados: ${user.name}")
            }
        } catch (e: Exception) {
            Log.e("AuthViewModel", "❌ Error refrescando datos de usuario: ${e.message}")
        }
    }
}

class AuthViewModelFactory(private val authRepository: AuthRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
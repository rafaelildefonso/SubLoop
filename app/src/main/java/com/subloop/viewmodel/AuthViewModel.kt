package com.subloop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.subloop.data.SubLoopRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val error: String? = null,
)

class AuthViewModel(private val repository: SubLoopRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(
        AuthUiState(isAuthenticated = repository.currentUserId != null),
    )
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) = authenticate {
        repository.login(email, password)
    }

    fun register(name: String, email: String, password: String) = authenticate {
        repository.register(name, email, password)
    }

    fun logout() {
        repository.logout()
        _uiState.value = AuthUiState()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    private fun authenticate(block: suspend () -> Result<Long>) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            val result = block()
            _uiState.value = result.fold(
                onSuccess = { AuthUiState(isAuthenticated = true) },
                onFailure = { AuthUiState(error = it.message ?: "Não foi possível continuar.") },
            )
        }
    }
}

class AuthViewModelFactory(
    private val repository: SubLoopRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        AuthViewModel(repository) as T
}

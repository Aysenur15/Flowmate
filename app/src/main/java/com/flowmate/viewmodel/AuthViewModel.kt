package com.flowmate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flowmate.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.flowmate.ui.component.AuthState
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun signUp(name: String, email: String, username: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState(isLoading = true)

            val result = authRepository.registerUser(name, email, username, password)

            result.onSuccess {
                _authState.value = AuthState(isAuthenticated = true, userName = it.username)
            }.onFailure {
                _authState.value = AuthState(errorMessage = it.message)
            }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState(isLoading = true)

            val result = authRepository.loginUser(email, password)

            result.onSuccess {
                _authState.value = AuthState(isAuthenticated = true, userName = it.username)
            }.onFailure {
                _authState.value = AuthState(errorMessage = it.message)
            }
        }
    }

    fun signOut() {
        authRepository.signOut()
        _authState.value = AuthState()
    }
}

package com.flowmate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flowmate.data.UserEntity
import com.flowmate.repository.AuthRepository
import com.flowmate.ui.component.AuthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // for greeting on HomeScreen
    private val _currentUserName = MutableStateFlow("")
    val currentUserName: StateFlow<String> = _currentUserName

    private val _user = MutableStateFlow<UserEntity?>(null)
    val user: StateFlow<UserEntity?> = _user.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    fun register(name: String, email: String, username: String, password: String) {
        viewModelScope.launch {
            _loading.value = true
            val result = authRepository.registerUser(name, email, username, password)
            result.onSuccess {
                _user.value = it
                _error.value = null
            }.onFailure {
                _user.value = null
                _error.value = it.message
            }
            _loading.value = false
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loading.value = true
            val result = authRepository.loginUser(email, password)
            result.onSuccess {
                _user.value = it
                _error.value = null
                android.util.Log.d("FlowMateNavGraph", "User log in succesful")
            }.onFailure {
                _user.value = null
                _error.value = it.message
                android.util.Log.d("FlowMateNavGraph", "User log in failed: ${it.message}")

            }
            _loading.value = false
        }
    }

    fun signOut() {
        authRepository.signOut()
        _user.value = null
        _error.value = null
    }
}

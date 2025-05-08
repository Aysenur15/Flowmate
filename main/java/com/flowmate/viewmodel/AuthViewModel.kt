package com.flowmate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    // outside-app vs inside-app flag
    private val _isUserLoggedIn = MutableStateFlow(false)
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn.asStateFlow()

    // for greeting on HomeScreen
    private val _currentUserName = MutableStateFlow("")
    val currentUserName: StateFlow<String> = _currentUserName.asStateFlow()

    fun signIn(username: String, password: String) {
        viewModelScope.launch {
            // TODO: call your real auth repo
            if (username.isNotBlank() && password.isNotBlank()) {
                _currentUserName.value = username
                _isUserLoggedIn.value = true
                /*loadStubData()*/
            }
        }
    }

    fun signUp(name: String, email: String, username: String, password: String) {
        viewModelScope.launch {
            // TODO: real sign-up logic
            _currentUserName.value = name
            _isUserLoggedIn.value = true
            /*loadStubData()*/
        }
    }

    fun signOut() {
        viewModelScope.launch {
            _isUserLoggedIn.value = false
            _currentUserName.value = ""
            /*_habits.value = emptyList()*/
        }
    }
}
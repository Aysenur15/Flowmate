package com.flowmate.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flowmate.ui.theme.ButtonShape
import com.flowmate.ui.theme.DisabledGray
import com.flowmate.ui.theme.OutlineBlue
import com.flowmate.ui.theme.TextFieldShape

// SignUpScreen is a composable function that displays the sign-up screen UI.
@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    onSignUp: (name: String, email: String, username: String, password: String) -> Unit,
    onNavigateToLogin: () -> Unit,
    loading: Boolean,
    error: String?
) {
    // State variables to hold user input
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    // Simple form check for validation
    val isFormValid = listOf(name, email, username, password).all { it.isNotBlank() }

    // Main container for the sign-up screen
    Box(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(Modifier.height(40.dp))

            // Name
            Text("Name", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(8.dp))
            TextField(
                value = name,
                onValueChange = { name = it },
                singleLine = true,
                placeholder = { Text("Enter your full name") },
                shape = TextFieldShape,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF59B7B2),
                    ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )
            Spacer(Modifier.height(24.dp))

            // Username
            Text("UserName", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(8.dp))
            TextField(
                value = username,
                onValueChange = { username = it },
                singleLine = true,
                placeholder = { Text("Pick a username") },
                shape = TextFieldShape,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF59B7B2),

                    ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )

            Spacer(Modifier.height(24.dp))

            // Email
            Text("Email", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(8.dp))
            TextField(
                value = email,
                onValueChange = { email = it },
                singleLine = true,
                placeholder = { Text("you@example.com") },
                shape = TextFieldShape,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF59B7B2),

                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )
            Spacer(Modifier.height(24.dp))

            // Password
            Text("Password", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(8.dp))
            TextField(
                value = password,
                onValueChange = { password = it },
                singleLine = true,
                placeholder = { Text("Choose a password") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon =
                        if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(
                        onClick = { passwordVisible = !passwordVisible },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(imageVector = icon, contentDescription = null)
                    }/*
                    n { passwordVisible = !passwordVisible } {
                        Icon(icon, contentDescription = if (passwordVisible) "Hide password" else "Show password")
                    }
                    */
                },
                shape = TextFieldShape,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF59B7B2),

                    ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )
            Spacer(Modifier.height(24.dp))

            Spacer(Modifier.height(32.dp))

            // Sign up button
            Button(
                onClick = { if (isFormValid) onSignUp(name, email, username, password) },
                enabled = isFormValid,
                shape = ButtonShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFormValid) MaterialTheme.colorScheme.primary else DisabledGray,
                    contentColor = if (isFormValid) MaterialTheme.colorScheme.onPrimary else Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Sign up", style = MaterialTheme.typography.titleMedium)
            }
        }

        if (loading){
            // Show loading indicator
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Loading...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
            }
        }
        if( error != null) {
            // Show error message
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Red
                )
            }
        }

        // Bottom "Sign up" link
        OutlinedButton(
            onClick = onNavigateToLogin,
            shape = ButtonShape,
            border = BorderStroke(2.dp, OutlineBlue),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = OutlineBlue),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Already have an account? Sign in",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
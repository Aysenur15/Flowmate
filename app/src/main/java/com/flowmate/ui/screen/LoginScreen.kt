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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flowmate.R
import com.flowmate.ui.theme.ButtonShape
import com.flowmate.ui.theme.DisabledGray
import com.flowmate.ui.theme.OutlineBlue
import com.flowmate.ui.theme.Peach
import com.flowmate.ui.theme.TextFieldShape

// 2. The LoginScreen composable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLogin: (String, String) -> Unit,
    onNavigateToSignUp: () -> Unit,
    loading: Boolean,
    error: String?

) {
    // UI state
    var username by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val isFormValid = email.isNotBlank() && password.isNotBlank()

    Box(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(40.dp))
            // TODO: Replace with your real logo
            Icon(
                painter = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = "FlowMate Logo",
                tint = Color.Unspecified,
                modifier = Modifier.size(96.dp)
            )
            Spacer(Modifier.height(48.dp))

            // Username / Email field
            Text(
                text = "Email",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 8.dp)
            )
            TextField(
                value = email,
                onValueChange = { email = it },
                singleLine = true,
                placeholder = { Text("Enter your email") },
                shape = TextFieldShape,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Peach,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )
            Spacer(Modifier.height(24.dp))

            // Password field
            Text(
                text = "Password",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 8.dp)
            )
            var passwordVisible by rememberSaveable { mutableStateOf(false) }
            TextField(
                value = password,
                onValueChange = { password = it },
                singleLine = true,
                placeholder = { Text("Enter password") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon =
                        if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            icon,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                shape = TextFieldShape,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Peach,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )
            Spacer(Modifier.height(32.dp))

            // Sign in button
            Button(
                onClick = { if (isFormValid) onLogin(email.trim(), password) },
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
                Text("Sign in", style = MaterialTheme.typography.titleMedium)
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
            onClick = onNavigateToSignUp,
            shape = ButtonShape,
            border = BorderStroke(2.dp, OutlineBlue),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = OutlineBlue),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Don’t have an account? Sign up",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
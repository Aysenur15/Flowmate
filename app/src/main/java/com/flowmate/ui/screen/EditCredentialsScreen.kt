package com.flowmate.ui.screen


import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest


fun updateCredentials(
    newUsername: String,
    newPassword: String,
    currentPassword: String,
    context: Context,
    onSuccess: () -> Unit,
    onFailure: (String) -> Unit
) {
    val user = FirebaseAuth.getInstance().currentUser
    val email = user?.email ?: return onFailure("User email not found")

    val credential = EmailAuthProvider.getCredential(email, currentPassword)

    user.reauthenticate(credential)
        .addOnSuccessListener {
            val tasks = mutableListOf<com.google.android.gms.tasks.Task<Void>>()

            if (newPassword.isNotBlank()) {
                tasks.add(user.updatePassword(newPassword))
            }

            if (newUsername.isNotBlank()) {
                val profileUpdate = UserProfileChangeRequest.Builder()
                    .setDisplayName(newUsername)
                    .build()
                tasks.add(user.updateProfile(profileUpdate))
            }

            Tasks.whenAllComplete(tasks)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { e -> onFailure(e.message ?: "Update failed") }

        }
        .addOnFailureListener { e ->
            onFailure("Re-authentication failed: ${e.message}")
        }
}



@Composable
fun EditCredentialsScreen(
    onSaveSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val context = LocalContext.current
    var newUsername by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var currentPassword by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Edit Password / Username", style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(
            value = newUsername,
            onValueChange = { newUsername = it },
            label = { Text("New Username (optional)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("New Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Button(
            onClick = { showDialog = true },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Save")
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    TextButton(onClick = {
                        // Call reauthentication logic here
                        updateCredentials(
                            newUsername = newUsername,
                            newPassword = newPassword,
                            currentPassword = currentPassword,
                            context = context,
                            onSuccess = {
                                onSaveSuccess()
                                showDialog = false
                            },
                            onFailure = {
                                onError(it)
                                showDialog = false
                            }
                        )
                    }) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                },
                title = { Text("Confirm Update") },
                text = {
                    Column {
                        Text("Enter your current password to proceed:")
                        OutlinedTextField(
                            value = currentPassword,
                            onValueChange = { currentPassword = it },
                            label = { Text("Current Password") },
                            visualTransformation = PasswordVisualTransformation()
                        )
                    }
                }
            )
        }

    }

}




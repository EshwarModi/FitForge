package com.fitforge.app.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen(
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit
) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),

        horizontalAlignment = Alignment.CenterHorizontally,

        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "FitForge",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Train Smarter. Live Stronger.",
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },

            label = {
                Text("Email")
            },

            modifier = Modifier.fillMaxWidth(),

            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),

            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },

            label = {
                Text("Password")
            },

            modifier = Modifier.fillMaxWidth(),

            visualTransformation =
                if (passwordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),

            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),

            verticalAlignment = Alignment.CenterVertically
        ) {

            Checkbox(
                checked = rememberMe,
                onCheckedChange = {
                    rememberMe = it
                }
            )

            Text("Remember Me")
        }

        TextButton(
            onClick = onForgotPasswordClick
        ) {
            Text("Forgot Password?")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(

            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),

            onClick = {

            }

        ) {

            Text("LOGIN")

        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedButton(

            modifier = Modifier.fillMaxWidth(),

            onClick = {

            }

        ) {

            Text("Continue with Google")

        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text("Don't have an account? ")

            TextButton(
                onClick = onRegisterClick
            ) {

                Text("Create Account")

            }

        }

    }

}
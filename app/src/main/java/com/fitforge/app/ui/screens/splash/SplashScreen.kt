package com.fitforge.app.ui.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.fitforge.app.ui.navigation.Routes
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController
) {

    LaunchedEffect(Unit) {

        delay(2000)

        navController.navigate(Routes.LOGIN) {
            popUpTo(Routes.SPLASH) {
                inclusive = true
            }
        }

    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),

        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "🏋️",
            style = MaterialTheme.typography.displayLarge
        )

        Text(
            text = "FitForge",
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            text = "Train Smarter. Live Stronger.",
            style = MaterialTheme.typography.bodyLarge
        )

        CircularProgressIndicator()

    }

}
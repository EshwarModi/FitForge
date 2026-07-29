package com.fitforge.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.fitforge.app.ui.navigation.AppNavigation
import com.fitforge.app.ui.theme.FitForgeTheme
import android.util.Log
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        Log.d("Firebase", "Firebase Initialized Successfully")

        setContent {
            FitForgeTheme {
                AppNavigation()
            }
        }
    }
}
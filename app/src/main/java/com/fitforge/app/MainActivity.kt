package com.fitforge.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.fitforge.app.ui.navigation.AppNavigation
import com.fitforge.app.ui.theme.FitForgeTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FitForgeTheme {
                AppNavigation()
            }
        }
    }
}
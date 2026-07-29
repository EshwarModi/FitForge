package com.fitforge.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fitforge.app.ui.screens.auth.LoginScreen
import com.fitforge.app.ui.screens.splash.SplashScreen
import com.fitforge.app.ui.screens.auth.RegisterScreen
import com.fitforge.app.ui.screens.auth.ForgotPasswordScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {

        composable(Routes.SPLASH) {
            SplashScreen(navController)
        }

        composable(Routes.LOGIN) {

            LoginScreen(

                onRegisterClick = {
                    navController.navigate(Routes.REGISTER)
                },

                onForgotPasswordClick = {
                    navController.navigate(Routes.FORGOT_PASSWORD)
                }

            )

        }

        composable(Routes.REGISTER) {

            RegisterScreen(

                onLoginClick = {
                    navController.popBackStack()
                }

            )

        }
        composable(Routes.FORGOT_PASSWORD) {

            ForgotPasswordScreen(

                onBackToLogin = {
                    navController.popBackStack()
                }

            )

        }

    }
}
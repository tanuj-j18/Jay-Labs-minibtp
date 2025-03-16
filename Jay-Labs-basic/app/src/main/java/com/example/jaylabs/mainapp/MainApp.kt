package com.example.jaylabs.mainapp

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.jaylabs.home.HomeScreen
import com.example.jaylabs.auth.AuthScreen
import com.example.jaylabs.auth.login.LogInScreen
import com.example.jaylabs.auth.signup.SignUpScreen
import com.example.jaylabs.pastreports.PastReportsScreen
import com.example.jaylabs.pastreports.ReportDetailScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun MainApp(
    auth: FirebaseAuth = FirebaseAuth.getInstance(),
    navController: NavHostController,
    modifier: Modifier
) {
    val user = auth.currentUser
    val startDestination = if (user == null) Route.AuthScreen.route else Route.HomeScreen.route

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.fillMaxSize(),
        enterTransition = {
            slideInHorizontally(initialOffsetX = { it }) + fadeIn()
        },
        exitTransition = {
            slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
        },
        popEnterTransition = {
            slideInHorizontally(initialOffsetX = { -it }) + fadeIn()
        },
        popExitTransition = {
            slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
        }
    ) {

        composable(Route.AuthScreen.route) {
            AuthScreen(navController = navController)
        }
        composable(Route.HomeScreen.route) {
          HomeScreen()
        }
        composable(Route.LogIn.route) {
            LogInScreen(navController = navController)
        }
        composable(Route.SignUp.route) {
            SignUpScreen(navController = navController)
        }
        composable(Route.PastReports.route) {
            PastReportsScreen(navController = navController)
        }
        composable("${Route.ReportDetails.route}/{reportName}/{reportDescription}") { backStackEntry ->
            val reportName = backStackEntry.arguments?.getString("reportName")
            val reportDescription = backStackEntry.arguments?.getString("reportDescription")
            ReportDetailScreen(reportName = reportName, reportDescription = reportDescription)
        }

    }
}


package com.example.jaylabs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.jaylabs.mainapp.MainApp
import com.example.jaylabs.mainapp.Route
import com.example.jaylabs.ui.theme.JayLabsTheme
import com.example.jaylabs.utils.DrawerContent
import com.example.jaylabs.utils.JayLabsTopAppBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            val topBarHiddenRoutes = listOf(
                Route.AuthScreen.route, Route.LogIn.route, Route.SignUp.route,
            )

            val drawerState =  rememberDrawerState(DrawerValue.Closed)
            val scope = rememberCoroutineScope()

            JayLabsTheme {
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        DrawerContent(
                            navController=navController,
                            scope = scope,
                            drawerState = drawerState
                        ) // Drawer content goes here
                    },
                    gesturesEnabled = currentRoute !in topBarHiddenRoutes
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            if (currentRoute !in topBarHiddenRoutes) {
                                JayLabsTopAppBar(
                                    navController,
                                    onPastReportsClick = {
                                        navController.navigate(Route.PastReports.route)
                                    },
                                    onMenuClick = {
                                        scope.launch { drawerState.open()

                                        } // Open drawer on menu click
                                    }
                                )
                            }
                        }
                    ) { innerPadding ->
                        MainApp(
                            navController = navController,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}

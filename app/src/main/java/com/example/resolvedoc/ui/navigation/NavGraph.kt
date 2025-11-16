package com.example.resolvedoc.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.resolvedoc.ui.screens.DashboardScreen
import com.example.resolvedoc.ui.screens.LoginScreen
import com.example.resolvedoc.ui.screens.PendenciasScreen
import com.example.resolvedoc.ui.screens.ReportScreen

@Composable
fun AppNavigation(startDestination: String = "login") {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {

        composable("login") {
            LoginScreen(onLoginSuccess = {
                navController.navigate("dashboard") { // ⬅️ Assume Dashboard é a tela principal após login
                    popUpTo("login") { inclusive = true }
                }
            })
        }


        composable("dashboard") {
            DashboardScreen(

                onOpenPendencias = { navController.navigate("pendencias") },
                onBack = { navController.popBackStack() },
                onNavigateToReports = { navController.navigate("reports") },
                onNewPendencia = { navController.navigate("new_pendencia") },
                onNavigateToProfile = { navController.navigate("profile") }
            )
        }

        composable("pendencias") {
            PendenciasScreen(

                onBack = { navController.popBackStack() }
            )
        }

        // Rota: REPORTS
        composable("reports") {
            ReportScreen(

                onBack = { navController.popBackStack() }
            )
        }
    }
}
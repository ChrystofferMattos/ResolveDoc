package com.example.resolvedoc.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.resolvedoc.ui.screens.DashboardScreen
import com.example.resolvedoc.ui.screens.LoginScreen
import com.example.resolvedoc.ui.screens.NewPendenciaScreen
import com.example.resolvedoc.ui.screens.PendenciaDetailScreen
import com.example.resolvedoc.ui.screens.PendenciasScreen
import com.example.resolvedoc.ui.screens.ProfileScreen
import com.example.resolvedoc.ui.screens.ReportScreen
import com.example.resolvedoc.ui.theme.ResolveDocTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ResolveDocTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost()
                }
            }
        }
    }
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            HomeScreen(
                onNavigateToDashboard = { navController.navigate("dashboard") },
                onNavigateToReports = { navController.navigate("reports") },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }

        composable("dashboard") {
            DashboardScreen(
                onBack = { navController.popBackStack() },
                onOpenPendencias = { navController.navigate("pendencias") },
                onNavigateToReports = { navController.navigate("reports") },
                onNewPendencia = { navController.navigate("new_pendencia") },
                onNavigateToProfile = { navController.navigate("profile") }
            )
        }

        composable("reports") {
            ReportScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable("new_pendencia") {
            NewPendenciaScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable("pendencias") {
            PendenciasScreen(
                onBack = { navController.popBackStack() },
                onPendenciaClick = { pendenciaId ->
                    navController.navigate("pendencia_detail/$pendenciaId")
                }
            )
        }

        composable(
            route = "pendencia_detail/{pendenciaId}"
        ) {
            PendenciaDetailScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable("profile") {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
fun HomeScreen(
    onNavigateToDashboard: () -> Unit,
    onNavigateToReports: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("TELA INICIAL (HOME) - Navegue abaixo:", modifier = Modifier)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onNavigateToDashboard) {
            Text("Ir para Dashboard")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = onNavigateToReports) {
            Text("Ir para Relatórios")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onLogout) {
            Text("Sair do Sistema")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ResolveDocTheme {
        AppNavHost()
    }
}

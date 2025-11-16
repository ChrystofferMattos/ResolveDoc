package com.example.resolvedoc.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.resolvedoc.ui.screens.DashboardScreen
import com.example.resolvedoc.ui.screens.LoginScreen
import com.example.resolvedoc.ui.screens.NewPendenciaScreen
import com.example.resolvedoc.ui.screens.PendenciasScreen
import com.example.resolvedoc.ui.screens.ProfileScreen
import com.example.resolvedoc.ui.theme.ResolveDocTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Card
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.text.style.TextAlign

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
        navController, "login"
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
                onBack = { navController.popBackStack("dashboard", false) }
            )
        }
        composable("new_pendencia") {
            NewPendenciaScreen(
                onBack = { navController.popBackStack("dashboard", false) }
            )
        }
        composable("pendencias") {
            PendenciasScreen(
                onBack = { navController.popBackStack()}
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
        Text("TELA INICIAL (HOME) - Navegue abaixo:", modifier = Modifier.padding(16.dp))

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
data class Estatistica(val title: String, val value: String, val secondary: String, val icon: ImageVector? = null, val valueColor: Color = Color.Unspecified)
data class ResumoMedico(val medico: String, val especialidade: String, val total: Int, val abertas: Int, val resolvidas: Int)

private val estatisticas = listOf(
    Estatistica("Total de Pendências", "4", "Análise completa das pendências", Icons.Default.Description),
    Estatistica("Taxa de Resolução", "25%", "1 de 4", valueColor = Color(0xFF4CAF50)), // Verde
    Estatistica("Pendências Abertas", "2", " ", valueColor = Color(0xFFF44336)), // Vermelho
    Estatistica("Tempo Médio", "5 dias", "Para resolução", Icons.Default.AccessTime)
)

private val resumoMedicos = listOf(
    ResumoMedico("Dr. João Silva", "Cardiologia", 2, 2, 0),
    ResumoMedico("Dr. Maria Jáca", "Pediatria", 1, 0, 1),
    ResumoMedico("Dr. Maria Pádua", "Ortopedia", 1, 0, 1)
)

val statusColors = mapOf("Abertas" to Color(0xFFF44336), "Em Análise" to Color(0xFFFFEB3B), "Resolvidas" to Color(0xFF4CAF50))
val medicoColors = mapOf("Dr. João Silva" to Color(0xFF2196F3), "Dr. Maria Jáca" to Color(0xFF4CAF50), "Dr. Maria Pádua" to Color(0xFFFFEB3B))


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = { ReportTopBar(onBack = onBack) },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {


            item {
                Text(
                    "Análise completa das pendências do RESOLVE DOC CEMAS",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }


            item { StatsGrid(estatisticas = estatisticas) }


            item {
                Text("Pendências por Status", style = MaterialTheme.typography.titleLarge)
                ChartsRow()
            }


            item {
                Text("Pendências por Tipo de Documento", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 16.dp))
                BarChartPlaceholder()
            }


            item {
                Text("Resumo Detalhado por Médico", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 16.dp))
                MedicoSummaryTable(medicos = resumoMedicos)
            }


            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportTopBar(onBack: () -> Unit) {
    TopAppBar(
        title = { Text("Relatórios e Estatísticas") },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
            }
        },
        actions = {

            Button(onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
                Icon(Icons.Default.Download, contentDescription = "Exportar PDF", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Exportar PDF")
            }
        }
    )
}

@Composable
fun StatsGrid(estatisticas: List<Estatistica>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCardReport(estatisticas[0], Modifier.weight(1f))
            StatCardReport(estatisticas[1], Modifier.weight(1f))
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCardReport(estatisticas[2], Modifier.weight(1f))
            StatCardReport(estatisticas[3], Modifier.weight(1f))
        }
    }
}

@Composable
fun StatCardReport(estatistica: Estatistica, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(estatistica.title, style = MaterialTheme.typography.bodyMedium)
                if (estatistica.icon != null) {
                    Icon(estatistica.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(estatistica.value, style = MaterialTheme.typography.headlineMedium, color = estatistica.valueColor)
            Text(estatistica.secondary, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun ChartsRow() {
    Row(
        modifier = Modifier.fillMaxWidth().height(250.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Placeholder Gráfico de Status (Pizza)
        Card(modifier = Modifier.weight(1f).fillMaxHeight()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxSize().padding(12.dp)) {
                Text("Pendências por Status", style = MaterialTheme.typography.titleMedium)
                PieChartPlaceholder(size = 100.dp) // Gráfico de Pizza
                ChartLegend(statusColors) // Legenda
            }
        }

        // Placeholder Gráfico por Médico (Pizza)
        Card(modifier = Modifier.weight(1f).fillMaxHeight()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxSize().padding(12.dp)) {
                Text("Pendências por Médico", style = MaterialTheme.typography.titleMedium)
                PieChartPlaceholder(size = 100.dp) // Gráfico de Pizza
                ChartLegend(medicoColors) // Legenda
            }
        }
    }
}

@Composable
fun BarChartPlaceholder() {
    Card(modifier = Modifier.fillMaxWidth().height(250.dp)) {
        Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.fillMaxSize().padding(16.dp)) {

            Row(modifier = Modifier.fillMaxWidth().height(200.dp), horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.Bottom) {
                repeat(4) {
                    Box(modifier = Modifier.width(30.dp).fillMaxHeight((it + 1) * 0.25f).background(MaterialTheme.colorScheme.primary))
                }
            }
        }
    }
}

@Composable
fun MedicoSummaryTable(medicos: List<ResumoMedico>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Cabeçalho da Tabela
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Médico", modifier = Modifier.weight(2f), style = MaterialTheme.typography.titleSmall)
                Text("Especialidade", modifier = Modifier.weight(2f), style = MaterialTheme.typography.titleSmall)
                Text("Total", modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleSmall, textAlign = TextAlign.Center)
                Text("Abertas", modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleSmall, textAlign = TextAlign.Center)
                Text("Resolvidas", modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleSmall, textAlign = TextAlign.Center)
            }
            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

            // Linhas de Dados
            medicos.forEach { medico ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(medico.medico, modifier = Modifier.weight(2f), style = MaterialTheme.typography.bodyMedium)
                    Text(medico.especialidade, modifier = Modifier.weight(2f), style = MaterialTheme.typography.bodyMedium)
                    Text(medico.total.toString(), modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
                    Text(medico.abertas.toString(), modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, color = if (medico.abertas > 0) Color(0xFFF44336) else MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                    Text(medico.resolvidas.toString(), modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, color = if (medico.resolvidas > 0) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                }
                HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
            }
        }
    }
}

@Composable
fun PieChartPlaceholder(size: Dp) {
    Canvas(modifier = Modifier.size(size)) {

        drawCircle(
            color = Color.LightGray,
            radius = size.toPx() / 2f,
            style = Stroke(width = size.toPx() / 4f, cap = StrokeCap.Butt)
        )
        drawLine(
            color = Color.DarkGray,
            start = Offset(center.x, 0f),
            end = Offset(center.x, size.toPx()),
            strokeWidth = 2.dp.toPx()
        )
    }
}

@Composable
fun ChartLegend(data: Map<String, Color>) {
    Column(modifier = Modifier.padding(top = 8.dp)) {
        data.forEach { (label, color) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(10.dp).background(color))
                Spacer(modifier = Modifier.width(4.dp))
                Text(label, style = MaterialTheme.typography.bodySmall)
            }
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

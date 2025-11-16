package com.example.resolvedoc.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.resolvedoc.feature.pendencias.presentation.ReportsUiState
import com.example.resolvedoc.feature.pendencias.presentation.ReportsViewModel
import com.example.resolvedoc.feature.pendencias.report.ReportPdfGenerator
import kotlinx.coroutines.launch
data class ReportStat(
    val title: String,
    val value: String,
    val secondary: String,
    val icon: ImageVector? = null,
    val valueColor: Color = Color.Unspecified
)

data class ResumoMedicoUi(
    val medico: String,
    val especialidade: String,
    val total: Int,
    val abertas: Int,
    val resolvidas: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    onBack: () -> Unit,
    viewModel: ReportsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        topBar = {
            ReportTopBar(
                onBack = onBack,
                onExportPdf = {
                    scope.launch {
                        ReportPdfGenerator.generate(context, state)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->

        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.total == 0 -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Ainda não há pendências registradas.")
                }
            }

            else -> {
                val estatisticas = montarEstatisticas(state)

                val resumoMedicos = state.resumoPorMedico.map { mr ->
                    ResumoMedicoUi(
                        medico = mr.medico,
                        especialidade = "",
                        total = mr.total,
                        abertas = mr.abertas,
                        resolvidas = mr.resolvidas
                    )
                }

                val statusColors = gerarCoresParaLabels(state.porStatus.keys)
                val medicoColors = gerarCoresParaLabels(
                    state.resumoPorMedico.map { it.medico }.toSet()
                )

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

                    item {
                        StatsGrid(estatisticas = estatisticas)
                    }

                    item {
                        Text(
                            "Pendências por Status",
                            style = MaterialTheme.typography.titleLarge
                        )
                        ChartsRow(
                            statusColors = statusColors,
                            medicoColors = medicoColors
                        )
                    }

                    item {
                        Text(
                            "Pendências por Tipo de Documento",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                        BarChartPlaceholder()
                    }

                    item {
                        Text(
                            "Resumo Detalhado por Médico",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                        MedicoSummaryTable(medicos = resumoMedicos)
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportTopBar(
    onBack: () -> Unit,
    onExportPdf: () -> Unit
) {
    TopAppBar(
        title = { Text("Relatórios e Estatísticas") },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
            }
        },
        actions = {
            Button(
                onClick = onExportPdf,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    Icons.Default.Download,
                    contentDescription = "Exportar PDF",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Exportar PDF")
            }
        }
    )
}

@Composable
fun StatsGrid(estatisticas: List<ReportStat>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCardReport(estatisticas[0], Modifier.weight(1f))
            StatCardReport(estatisticas[1], Modifier.weight(1f))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCardReport(estatisticas[2], Modifier.weight(1f))
            StatCardReport(estatisticas[3], Modifier.weight(1f))
        }
    }
}

@Composable
fun StatCardReport(estatistica: ReportStat, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(estatistica.title, style = MaterialTheme.typography.bodyMedium)
                estatistica.icon?.let {
                    Icon(it, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                estatistica.value,
                style = MaterialTheme.typography.headlineMedium,
                color = estatistica.valueColor
            )
            Text(estatistica.secondary, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun ChartsRow(
    statusColors: Map<String, Color>,
    medicoColors: Map<String, Color>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                Text("Pendências por Status", style = MaterialTheme.typography.titleMedium)
                PieChartPlaceholder(size = 100.dp)
                ChartLegend(statusColors)
            }
        }

        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                Text("Pendências por Médico", style = MaterialTheme.typography.titleMedium)
                PieChartPlaceholder(size = 100.dp)
                ChartLegend(medicoColors)
            }
        }
    }
}

@Composable
fun BarChartPlaceholder() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    ) {
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.Bottom
            ) {
                repeat(4) {
                    Box(
                        modifier = Modifier
                            .width(30.dp)
                            .fillMaxHeight((it + 1) * 0.25f)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }
    }
}

@Composable
fun MedicoSummaryTable(medicos: List<ResumoMedicoUi>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Médico",
                    modifier = Modifier.weight(2f),
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    "Especialidade",
                    modifier = Modifier.weight(2f),
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    "Total",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.Center
                )
                Text(
                    "Abertas",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.Center
                )
                Text(
                    "Resolvidas",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.Center
                )
            }
            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

            medicos.forEach { medico ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        medico.medico,
                        modifier = Modifier.weight(2f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        medico.especialidade,
                        modifier = Modifier.weight(2f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        medico.total.toString(),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        medico.abertas.toString(),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (medico.abertas > 0) Color(0xFFF44336)
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        medico.resolvidas.toString(),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (medico.resolvidas > 0) Color(0xFF4CAF50)
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
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
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(color)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(label, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

private fun gerarCoresParaLabels(labels: Set<String>): Map<String, Color> {
    if (labels.isEmpty()) return emptyMap()

    val palette = listOf(
        Color(0xFFF44336),
        Color(0xFFFFC107),
        Color(0xFF4CAF50),
        Color(0xFF2196F3),
        Color(0xFF9C27B0),
        Color(0xFFFF9800),
        Color(0xFF009688)
    )

    val map = mutableMapOf<String, Color>()
    var index = 0

    labels.forEach { label ->
        val color = if (index < palette.size) palette[index] else Color.Gray
        map[label] = color
        index++
    }

    return map
}

@Composable
private fun montarEstatisticas(state: ReportsUiState): List<ReportStat> {
    val taxa = "${state.taxaResolucao}%"
    val resolucaoDescricao = "${state.resolvidas} de ${state.total}"

    val tempoMedioTexto = state.tempoMedioResolucaoDias?.let {
        String.format("%.1f dias", it)
    } ?: "N/D"

    return listOf(
        ReportStat(
            title = "Total de Pendências",
            value = state.total.toString(),
            secondary = "Análise completa das pendências",
            icon = Icons.Default.Description
        ),
        ReportStat(
            title = "Taxa de Resolução",
            value = taxa,
            secondary = resolucaoDescricao,
            valueColor = Color(0xFF4CAF50)
        ),
        ReportStat(
            title = "Pendências Abertas",
            value = state.abertas.toString(),
            secondary = " ",
            valueColor = Color(0xFFF44336)
        ),
        ReportStat(
            title = "Tempo Médio",
            value = tempoMedioTexto,
            secondary = "Para resolução",
            icon = Icons.Default.AccessTime
        )
    )
}


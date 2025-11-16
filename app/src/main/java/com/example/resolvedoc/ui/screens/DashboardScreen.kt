package com.example.resolvedoc.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.resolvedoc.feature.pendencias.presentation.DashboardViewModel
import com.google.firebase.auth.FirebaseAuth

data class PendenciaRecente(val titulo: String, val subtitulo: String, val status: String, val corStatus: Color)

@Composable
fun DashboardScreen(
    onBack: () -> Unit,
    onOpenPendencias: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNewPendencia: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    val currentUser = FirebaseAuth.getInstance().currentUser

    val userName = currentUser?.displayName
        ?: currentUser?.email?.substringBefore("@")
        ?: "Coordenador(a)"

    val pendenciasRecentes = state.pendenciasRecentes.map { p ->
        val corStatus = when (p.status) {
            "Aberta" -> MaterialTheme.colorScheme.error
            "Em Análise" -> MaterialTheme.colorScheme.tertiary
            "Resolvida" -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        }

        PendenciaRecente(
            titulo = p.medico.ifBlank { "Sem médico" },
            subtitulo = p.tipo.ifBlank { p.descricao.take(30) },
            status = p.status.ifBlank { "Sem status" },
            corStatus = corStatus
        )
    }

    Scaffold(
        topBar = { DashboardTopBar(onBack = onBack) },
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
                    "Dashboard",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
                Text(
                    "Bem-vindo(a), $userName",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    "Aguardando resolução",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            item {
                StatsRow(
                    abertas = state.abertas,
                    resolvidas = state.resolvidas,
                    tempoMedioResolucaoDias = state.tempoMedioResolucaoDias,
                    onOpenPendencias = onOpenPendencias,
                    onNavigateToReports = onNavigateToReports
                )
            }

            item {
                ActionsCard(
                    onNewPendencia = onNewPendencia,
                    onViewAll = onOpenPendencias,
                    onGenerateReports = onNavigateToReports,
                    onNavigateToProfile = onNavigateToProfile
                )
            }

            item {
                Text(
                    "Pendências Recentes",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            if (pendenciasRecentes.isEmpty()) {
                item {
                    Text(
                        "Nenhuma pendência recente.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(pendenciasRecentes) { pendencia ->
                    PendenciaItem(pendencia = pendencia)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardTopBar(onBack: () -> Unit) {

    CenterAlignedTopAppBar(
        title = { Text("RESOLVE DOC", style = MaterialTheme.typography.titleLarge) },

        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

@Composable
fun StatsRow(
    abertas: Int,
    resolvidas: Int,
    tempoMedioResolucaoDias: Double?,
    onOpenPendencias: () -> Unit,
    onNavigateToReports: () -> Unit
) {
    val tempoMedioTexto = tempoMedioResolucaoDias?.let {
        // Ex: "5.2 dias"
        String.format("%.1f dias", it)
    } ?: "N/D"

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        StatCard(
            title = "Pendências Abertas",
            value = abertas.toString(),
            icon = Icons.AutoMirrored.Filled.List,
            modifier = Modifier.weight(1f),
            onClick = onOpenPendencias
        )
        StatCard(
            title = "Pendências Resolvidas",
            value = resolvidas.toString(),
            icon = Icons.Default.CheckCircle,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "Tempo Médio de Resolução",
            value = tempoMedioTexto,
            icon = Icons.Default.AccessTime,
            modifier = Modifier.weight(1f),
            onClick = onNavigateToReports
        )
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        onClick = onClick ?: {}
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(title, style = MaterialTheme.typography.bodyMedium)
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall)
        }
    }
}


@Composable
fun ActionsCard(onNewPendencia: () -> Unit, onViewAll: () -> Unit, onGenerateReports: () -> Unit,onNavigateToProfile: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Ações", style = MaterialTheme.typography.titleMedium)

            Button(onClick = onNewPendencia, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) {
                Icon(Icons.Default.Add, contentDescription = "Nova Pendência")
                Spacer(Modifier.width(8.dp))
                Text("Nova Pendência")
            }

            OutlinedButton(onClick = onViewAll, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) {
                Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Ver Todas as Pendências")
                Spacer(Modifier.width(8.dp))
                Text("Ver Todas as Pendências")
            }

            OutlinedButton(onClick = onGenerateReports, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) {
                Icon(Icons.Default.PieChart, contentDescription = "Gerar Relatórios")
                Spacer(Modifier.width(8.dp))
                Text("Gerar Relatórios")
            }
            OutlinedButton(onClick = onNavigateToProfile, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) {
                Icon(Icons.Default.Person, contentDescription = "Perfil")
                Spacer(Modifier.width(8.dp))
                Text("Perfil do Usuário")
            }
        }
    }
}

@Composable
fun PendenciaItem(pendencia: PendenciaRecente) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(pendencia.titulo, style = MaterialTheme.typography.titleMedium)
                Text(pendencia.subtitulo, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            AssistChip(
                onClick = {  },
                label = { Text(pendencia.status) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = pendencia.corStatus.copy(alpha = 0.1f),
                    labelColor = pendencia.corStatus
                )
            )
        }
    }
}


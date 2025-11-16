package com.example.resolvedoc.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class PendenciaUi(val id: String, val medico: String, val tipo: String, val descricao: String, val unidade: String, val status: String)

@Composable
fun PendenciasScreen(
    onBack: () -> Unit,
) {
    val sample = listOf(
        PendenciaUi("1","Dr. João Silva","Receita","Receita com erro na dosagem","Unidade Básica de Saúde","Aberta"),
        PendenciaUi("2","Dr. Maria Jáca","Atestado","Atestado sem carimbo","Unidade Básica de Saúde","Em Análise"),
        PendenciaUi("3","Dr. Maria Pádua","Receita","Receita ilegível","CEMAS - Centro","Resolvida")
    )

    Column(modifier = Modifier.fillMaxSize()) {


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 8.dp, end = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Lista de Pendências", style = MaterialTheme.typography.headlineSmall)
        }

        // 2. LISTA DE PENDÊNCIAS (LazyColumn)
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
        ) {
            items(sample) { p ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(p.medico, style = MaterialTheme.typography.titleMedium)
                        Text(p.tipo)
                        Text(p.descricao)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Unidade: ${p.unidade}", style = MaterialTheme.typography.bodySmall)
                            Text(p.status, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}
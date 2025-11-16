package com.example.resolvedoc.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.resolvedoc.feature.pendencias.presentation.NewPendenciaViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Unspecified


val tiposPendencia = listOf("Receita", "Atestado", "Prontuário", "Outro")

@Composable
fun NewPendenciaScreen(
    onBack: () -> Unit,
    viewModel: NewPendenciaViewModel = hiltViewModel()
) {

    val isSaved by viewModel.isSaved.collectAsState()

    var medico by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var unidade by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }


    LaunchedEffect(isSaved) {
        if (isSaved) {
            isSaving = false
            onBack()
            viewModel.resetSavedState()
        }
    }

    Scaffold(
        topBar = { NewPendenciaTopBar(onBack = onBack) },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                "Nova Pendência Documental",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = medico,
                onValueChange = { medico = it },
                label = { Text("Nome do Médico") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            )

            TipoDropdown(
                selectedTipo = tipo,
                onTipoSelected = { tipo = it },
                tipos = tiposPendencia
            )

            OutlinedTextField(
                value = unidade,
                onValueChange = { unidade = it },
                label = { Text("Unidade de Saúde") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            )

            OutlinedTextField(
                value = descricao,
                onValueChange = { descricao = it },
                label = { Text("Detalhes e Descrição do Erro") },
                minLines = 4,
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
            )

            Button(
                onClick = {
                    if (!isSaving) {
                        isSaving = true
                        viewModel.savePendencia(medico, tipo, descricao, unidade)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = medico.isNotBlank() && tipo.isNotBlank() && descricao.isNotBlank() && unidade.isNotBlank() && !isSaving // Validação de todos os campos
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("REGISTRAR PENDÊNCIA")
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPendenciaTopBar(onBack: () -> Unit) {
    CenterAlignedTopAppBar(
        title = { Text("Nova Pendência") },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = Color.Unspecified,
            navigationIconContentColor = Color.Unspecified,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = Color.Unspecified
        )
    )
}


@Composable

fun TipoDropdown(selectedTipo: String, onTipoSelected: (String) -> Unit, tipos: List<String>) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        OutlinedTextField(
            readOnly = true,
            value = if (selectedTipo.isBlank()) "Selecione o Tipo" else selectedTipo,
            onValueChange = { },
            label = { Text("Tipo de Pendência") },

            // ⬅️ Ícone de seta simples
            trailingIcon = {
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Expandir")
            },

            // ⬅️ AÇÃO PRINCIPAL: Adiciona o Modifier.clickable para abrir o menu
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
        )

        // ⬅️ Usa o DropdownMenu simples (não Exposed)
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            // Garante que o menu tenha a mesma largura do TextField
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            tipos.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        onTipoSelected(selectionOption)
                        expanded = false
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
package com.example.resolvedoc.ui.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class UserProfile(
    val nome: String,
    val email: String,
    val cargo: String,
    val permissoes: List<String>
)

val sampleProfile = UserProfile(
    nome = "Maria Coordenadora",
    email = "coordenadora@cemas.com",
    cargo = "Coordenadora RESOLVE DOC",
    permissoes = listOf(
        "Visualizar todas as pendências",
        "Criar e gerenciar pendências",
        "Gerar relatórios e estatísticas",
        "Acesso a todas as unidades"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onBack: () -> Unit, onLogout: () -> Unit) {
    val profile = sampleProfile

    var senhaAtual by remember { mutableStateOf("") }
    var novaSenha by remember { mutableStateOf("") }
    var confirmarNovaSenha by remember { mutableStateOf("") }

    Scaffold(
        topBar = { ProfileTopBar(onBack = onBack) },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                UserHeaderCard(profile = profile, onLogout = onLogout)
            }

            item {
                ChangePasswordCard(
                    senhaAtual, { senhaAtual = it },
                    novaSenha, { novaSenha = it },
                    confirmarNovaSenha, { confirmarNovaSenha = it }
                )
            }

            item {
                PermissionsCard(permissoes = profile.permissoes)
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopBar(onBack: () -> Unit) {
    CenterAlignedTopAppBar(
        title = { Text("Perfil do Usuário") },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
            }
        }
    )
}

@Composable
fun UserHeaderCard(profile: UserProfile, onLogout: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Avatar (Placeholder)
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(profile.nome.first().toString(), style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onPrimary)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(profile.nome, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Text(profile.cargo, style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Sair da Conta")
                }
            }

            Spacer(modifier = Modifier.width(16.dp))


            Column(modifier = Modifier.weight(1f)) {
                Text("Informações Pessoais", style = MaterialTheme.typography.labelLarge)
                Divider(modifier = Modifier.padding(vertical = 4.dp))
                ProfileItem("Nome Completo", profile.nome, Icons.Default.Person)
                ProfileItem("E-mail", profile.email, Icons.Default.Email)
                ProfileItem("Perfil de Acesso", profile.cargo, Icons.Default.VerifiedUser)
            }
        }
    }
}

@Composable
fun ProfileItem(label: String, value: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall)
            Text(value, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun ChangePasswordCard(
    senhaAtual: String, onSenhaAtualChange: (String) -> Unit,
    novaSenha: String, onNovaSenhaChange: (String) -> Unit,
    confirmarNovaSenha: String, onConfirmarNovaSenhaChange: (String) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Alterar Senha", style = MaterialTheme.typography.labelLarge)
            Divider(modifier = Modifier.padding(vertical = 4.dp))

            OutlinedTextField(value = senhaAtual, onValueChange = onSenhaAtualChange, label = { Text("Senha Atual") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = novaSenha, onValueChange = onNovaSenhaChange, label = { Text("Nova Senha") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = confirmarNovaSenha, onValueChange = onConfirmarNovaSenhaChange, label = { Text("Confirmar Nova Senha") }, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {  }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Lock, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Alterar Senha")
            }
        }
    }
}

@Composable
fun PermissionsCard(permissoes: List<String>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Permissões de Acesso", style = MaterialTheme.typography.labelLarge)
            Divider(modifier = Modifier.padding(vertical = 4.dp))

            permissoes.forEach { permissao ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Green.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(permissao, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
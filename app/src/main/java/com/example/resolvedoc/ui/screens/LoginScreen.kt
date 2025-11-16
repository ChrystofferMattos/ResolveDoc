package com.example.resolvedoc.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.resolvedoc.feature.auth.presentation.LoginViewModel
import com.example.resolvedoc.feature.auth.presentation.LoginUiState


@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,

    viewModel: LoginViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsState()


    var passwordVisible by remember { mutableStateOf(false) }


    LaunchedEffect(state.isLoginSuccess) {
        if (state.isLoginSuccess) {
            onLoginSuccess()
            viewModel.resetLoginState()
        }
    }


    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text("RESOLVE DOC", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))


        OutlinedTextField(
            value = state.username,
            onValueChange = viewModel::onUsernameChange,
            label = { Text("E-mail") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))


        OutlinedTextField(
            value = state.password,
            onValueChange = viewModel::onPasswordChange,
            label = { Text("Senha") },


            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                val description = if (passwordVisible) "Esconder senha" else "Mostrar senha"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = description)
                }
            },

            modifier = Modifier.fillMaxWidth()
        )


        if (state.error != null) {
            Text(
                state.error!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))


        Button(
            onClick = viewModel::login,
            modifier = Modifier.fillMaxWidth(),

            enabled = !state.isLoading
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(" Entrar ")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("Contas de teste:\ncoordenadora@cemas.com\njoana.costa@cemas.com.br\njoao.silva@cemas.com\nSenha: 123456", style = MaterialTheme.typography.bodyMedium)
    }
}
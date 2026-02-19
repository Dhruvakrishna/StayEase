package com.example.stayease.feature.auth
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onLoggedIn: () -> Unit, vm: LoginViewModel = hiltViewModel()) {
  val state by vm.state.collectAsState()
  LaunchedEffect(state.success) {
    if (state.success) { vm.consumeSuccess(); onLoggedIn() }
  }

  Scaffold(topBar = { TopAppBar(title = { Text("Sign in") }) }) { padding ->
    Column(
      modifier = Modifier.padding(padding).padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Text("Firebase Auth is wired after you add google-services.json. Demo auth works now.")
      OutlinedTextField(state.email, vm::onEmail, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
      OutlinedTextField(state.password, vm::onPassword, label = { Text("Password") }, modifier = Modifier.fillMaxWidth(), singleLine = true, visualTransformation = PasswordVisualTransformation())
      if (state.error != null) Text(state.error!!, color = MaterialTheme.colorScheme.error)
      Button(onClick = vm::submit, enabled = !state.loading, modifier = Modifier.fillMaxWidth()) {
        if (state.loading) {
          CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
          Spacer(Modifier.width(10.dp))
        }
        Text("Login")
      }
    }
  }
}

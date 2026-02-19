package com.example.stayease.feature.asyncdemo
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AsyncDemoScreen(onBack: () -> Unit, vm: AsyncDemoViewModel = hiltViewModel()) {
  val state by vm.state.collectAsState()
  Scaffold(
    topBar = { TopAppBar(title = { Text("Async + Scaling Demo") }, navigationIcon = { TextButton(onClick = onBack) { Text("Back") } }) }
  ) { padding ->
    Column(Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
      Text(state.resultText)
      if (state.lastMs != null) Text("Last run: ${state.lastMs} ms")
      Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Button(onClick = vm::runParallelQueries, enabled = !state.running) { Text("Parallel") }
        OutlinedButton(onClick = vm::runRetryBackoff, enabled = !state.running) { Text("Retry") }
      }
      OutlinedButton(onClick = vm::cancel, enabled = state.running) { Text("Cancel") }
    }
  }
}

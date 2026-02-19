package com.example.stayease.feature.bookings
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingsScreen(onBack: () -> Unit, vm: BookingsViewModel = hiltViewModel()) {
  val bookings by vm.bookings.collectAsState(initial = emptyList())
  val fmt = remember { SimpleDateFormat("MMM d, yyyy h:mm a", Locale.US) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("My Bookings") },
        navigationIcon = { TextButton(onClick = onBack) { Text("Back") } },
        actions = { TextButton(onClick = vm::syncNow) { Text("Sync") } }
      )
    }
  ) { padding ->
    LazyColumn(
      modifier = Modifier.padding(padding).fillMaxSize(),
      contentPadding = PaddingValues(12.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      if (bookings.isEmpty()) {
        item { Text("No bookings yet. Create one from any stay.", modifier = Modifier.padding(8.dp)) }
      } else {
        items(bookings.size) { idx ->
          val b = bookings[idx]
          Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Text(b.stayName, style = MaterialTheme.typography.titleMedium)
              Text("Guests: ${b.guests} • Rooms: ${b.rooms} • ${b.roomType}")
              Text("Total: ${b.currency} ${"%.2f".format(b.totalAmount)}")
              Text("Status: ${b.status}")
              if (b.confirmationCode != null) Text("Confirmation: ${b.confirmationCode}")
              Text("Created: ${fmt.format(Date(b.createdAtEpochMs))}", style = MaterialTheme.typography.bodySmall)
            }
          }
        }
      }
    }
  }
}

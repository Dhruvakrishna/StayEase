package com.example.stayease.feature.details
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StayDetailsScreen(onBack: () -> Unit, onBookings: () -> Unit, vm: StayDetailsViewModel = hiltViewModel()) {
  val state by vm.state.collectAsState()

  if (state.message != null) {
    AlertDialog(
      onDismissRequest = { vm.clearMessage() },
      confirmButton = { TextButton(onClick = { vm.clearMessage(); onBookings() }) { Text("View bookings") } },
      dismissButton = { TextButton(onClick = { vm.clearMessage() }) { Text("Close") } },
      title = { Text("Status") },
      text = { Text(state.message ?: "") }
    )
  }

  Scaffold(
    topBar = { TopAppBar(title = { Text("Stay Details") }, navigationIcon = { TextButton(onClick = onBack) { Text("Back") } }) }
  ) { padding ->
    Box(Modifier.padding(padding).fillMaxSize()) {
      when {
        state.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        state.error != null -> Column(Modifier.padding(16.dp)) { Text(state.error ?: "Error") }
        else -> {
          val stay = state.stay!!
          Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(stay.name, style = MaterialTheme.typography.titleLarge)
            Text(stay.address ?: "Address not available")
            Text("$${stay.nightlyPriceUsdEstimate}/night (est.)")
            if (stay.rating != null) Text(String.format("★ %.1f", stay.rating))
            Divider()
            Text("Booking details", style = MaterialTheme.typography.titleMedium)

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              OutlinedButton(onClick = vm::decNights) { Text("-") }
              Text("Nights: ${state.nights}", modifier = Modifier.padding(top = 10.dp))
              OutlinedButton(onClick = vm::incNights) { Text("+") }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              OutlinedButton(onClick = vm::decGuests) { Text("-") }
              Text("Guests: ${state.guests}", modifier = Modifier.padding(top = 10.dp))
              OutlinedButton(onClick = vm::incGuests) { Text("+") }
            }

            OutlinedTextField(value = state.roomType, onValueChange = vm::setRoomType, label = { Text("Room type") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = state.specialRequests, onValueChange = vm::setRequests, label = { Text("Special requests (optional)") }, modifier = Modifier.fillMaxWidth())

            Button(onClick = vm::book, modifier = Modifier.fillMaxWidth()) { Text("Create booking") }
          }
        }
      }
    }
  }
}

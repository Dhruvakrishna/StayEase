package com.example.stayease.feature.details

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.stayease.domain.model.PointOfInterest
import com.example.stayease.domain.model.Review
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StayDetailsScreen(
    onBack: () -> Unit,
    onBookings: () -> Unit,
    vm: StayDetailsViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = state.checkInEpochDay * 24 * 60 * 60 * 1000
    )
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedPoiForTrip by remember { mutableStateOf<PointOfInterest?>(null) }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        vm.setCheckInDate(it / (24 * 60 * 60 * 1000))
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (selectedPoiForTrip != null) {
        AlertDialog(
            onDismissRequest = { selectedPoiForTrip = null },
            title = { Text("Add to Trip") },
            text = {
                Column {
                    Text("Select a trip to add ${selectedPoiForTrip?.name}:")
                    Spacer(Modifier.height(8.dp))
                    state.trips.forEach { trip ->
                        ListItem(
                            headlineContent = { Text(trip.title) },
                            modifier = Modifier.clickable {
                                vm.addItemToTrip(selectedPoiForTrip!!, trip.id)
                                selectedPoiForTrip = null
                            }
                        )
                    }
                    if (state.trips.isEmpty()) {
                        Text("No active trips found. Create one in 'Trip Plans'.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            confirmButton = {},
            dismissButton = { TextButton(onClick = { selectedPoiForTrip = null }) { Text("Cancel") } }
        )
    }

    if (state.message != null) {
        AlertDialog(
            onDismissRequest = { vm.clearMessage() },
            confirmButton = {
                TextButton(onClick = { vm.clearMessage() }) { Text("OK") }
            },
            title = { Text("Information") },
            text = { Text(state.message ?: "") }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.stay?.name ?: "Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Share */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                }
            )
        }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            AnimatedContent(
                targetState = state.loading,
                transitionSpec = { fadeIn(tween(500)) togetherWith fadeOut(tween(500)) }
            ) { loading ->
                if (loading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (state.stay != null) {
                    val stay = state.stay!!
                    Column(
                        modifier = Modifier
                            .verticalScroll(scrollState)
                            .fillMaxSize()
                    ) {
                        // Enhanced Image Gallery
                        Box {
                            if (stay.imageUrls.isNotEmpty()) {
                                val pagerState = rememberPagerState(pageCount = { stay.imageUrls.size })
                                HorizontalPager(
                                    state = pagerState,
                                    modifier = Modifier.fillMaxWidth().height(320.dp)
                                ) { page ->
                                    AsyncImage(
                                        model = stay.imageUrls[page],
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                // Gradient Overlay
                                Box(
                                    Modifier.fillMaxWidth().height(80.dp).align(Alignment.BottomCenter)
                                        .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))))
                                )
                                // Indicator
                                Row(
                                    Modifier.padding(16.dp).align(Alignment.BottomCenter),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    repeat(stay.imageUrls.size) { iteration ->
                                        val color = if (pagerState.currentPage == iteration) Color.White else Color.White.copy(alpha = 0.5f)
                                        Box(Modifier.padding(3.dp).clip(CircleShape).background(color).size(if (pagerState.currentPage == iteration) 10.dp else 6.dp))
                                    }
                                }
                            }
                        }

                        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                            // Header & Weather
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text(stay.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
                                    Text(stay.address ?: "", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                if (state.weatherTemp != null) {
                                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                                        Row(Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.WbSunny, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(Modifier.width(4.dp))
                                            Text("${state.weatherTemp?.toInt()}°C", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            // Travel Companion POIs
                            if (state.pois.isNotEmpty()) {
                                Column {
                                    Text("Nearby Attractions", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                    Spacer(Modifier.height(12.dp))
                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                        items(state.pois) { poi ->
                                            PoiCard(
                                                poi = poi,
                                                onAdd = { selectedPoiForTrip = poi },
                                                onNavigate = {
                                                    val gmmIntentUri = Uri.parse("google.navigation:q=${poi.location.lat},${poi.location.lon}")
                                                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                                    mapIntent.setPackage("com.google.android.apps.maps")
                                                    context.startActivity(mapIntent)
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            // Booking Info
                            Text("Booking Details", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            OutlinedCard(
                                onClick = { showDatePicker = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(Modifier.width(16.dp))
                                    Column {
                                        Text("Check-in", style = MaterialTheme.typography.labelMedium)
                                        Text(SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(Date(state.checkInEpochDay * 24 * 60 * 60 * 1000)), fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(Modifier.weight(1f))
                                    Text("${state.nights} nights", style = MaterialTheme.typography.bodyMedium)
                                }
                            }

                            Button(
                                onClick = vm::book,
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text("Book for $${stay.nightlyPriceUsdEstimate * state.nights}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.height(20.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PoiCard(poi: PointOfInterest, onAdd: () -> Unit, onNavigate: () -> Unit) {
    Card(
        modifier = Modifier.width(200.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(Modifier.fillMaxWidth().height(100.dp).background(MaterialTheme.colorScheme.secondaryContainer)) {
                Icon(Icons.Default.Place, contentDescription = null, modifier = Modifier.align(Alignment.Center).size(32.dp), tint = MaterialTheme.colorScheme.onSecondaryContainer)
            }
            Column(Modifier.padding(12.dp)) {
                Text(poi.name, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(poi.category, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilledIconButton(onClick = onAdd, modifier = Modifier.size(36.dp), colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primary)) {
                        Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(18.dp))
                    }
                    OutlinedIconButton(onClick = onNavigate, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Navigation, contentDescription = "Navigate", modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

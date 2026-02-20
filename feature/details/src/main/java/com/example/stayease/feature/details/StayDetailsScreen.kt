package com.example.stayease.feature.details

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
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

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = state.checkInEpochDay * 24 * 60 * 60 * 1000
    )
    var showDatePicker by remember { mutableStateOf(false) }

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

    if (state.message != null) {
        AlertDialog(
            onDismissRequest = { vm.clearMessage() },
            confirmButton = {
                TextButton(onClick = {
                    vm.clearMessage()
                    onBookings()
                }) { Text("View bookings") }
            },
            dismissButton = {
                TextButton(onClick = { vm.clearMessage() }) { Text("Close") }
            },
            title = { Text("Status") },
            text = { Text(state.message ?: "") }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Stay Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                )
            )
        }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            when {
                state.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                state.error != null -> Column(Modifier.padding(16.dp)) {
                    Text(state.error ?: "Error", color = MaterialTheme.colorScheme.error)
                    Button(onClick = { /* Retry */ }) { Text("Retry") }
                }
                state.stay != null -> {
                    val stay = state.stay!!
                    Column(
                        modifier = Modifier
                            .verticalScroll(scrollState)
                            .fillMaxSize()
                    ) {
                        // Image Gallery with Pager
                        if (stay.imageUrls.isNotEmpty()) {
                            val pagerState = rememberPagerState(pageCount = { stay.imageUrls.size })
                            Box {
                                HorizontalPager(
                                    state = pagerState,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(300.dp)
                                ) { page ->
                                    AsyncImage(
                                        model = stay.imageUrls[page],
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                // Page Indicator
                                Row(
                                    Modifier
                                        .height(50.dp)
                                        .fillMaxWidth()
                                        .align(Alignment.BottomCenter),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    repeat(stay.imageUrls.size) { iteration ->
                                        val color = if (pagerState.currentPage == iteration) Color.White else Color.White.copy(alpha = 0.5f)
                                        Box(
                                            modifier = Modifier
                                                .padding(4.dp)
                                                .clip(CircleShape)
                                                .background(color)
                                                .size(8.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                            // Header Information
                            Column {
                                Text(
                                    stay.name,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Spacer(Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color(0xFFFFB300))
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        "${stay.rating ?: "N/A"} • ${stay.category.replaceFirstChar { it.uppercase() }}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            // Amenities Section
                            if (stay.amenities.isNotEmpty()) {
                                Column {
                                    Text("Top Amenities", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Spacer(Modifier.height(8.dp))
                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        items(stay.amenities) { amenity ->
                                            SuggestionChip(
                                                onClick = { },
                                                label = { Text(amenity) },
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            HorizontalDivider()

                            // Booking Card
                            Text("Book Your Stay", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            
                            OutlinedCard(
                                onClick = { showDatePicker = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.DateRange, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(Modifier.width(16.dp))
                                    Column {
                                        Text("Check-in Date", style = MaterialTheme.typography.labelMedium)
                                        Text(
                                            SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
                                                .format(Date(state.checkInEpochDay * 24 * 60 * 60 * 1000)),
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Counter(label = "Nights", value = state.nights, onIncrement = vm::incNights, onDecrement = vm::decNights, modifier = Modifier.weight(1f))
                                Counter(label = "Guests", value = state.guests, onIncrement = vm::incGuests, onDecrement = vm::decGuests, modifier = Modifier.weight(1f))
                            }

                            // User Reviews
                            if (stay.reviews.isNotEmpty()) {
                                HorizontalDivider()
                                Column {
                                    Text("User Reviews", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Spacer(Modifier.height(12.dp))
                                    stay.reviews.forEach { review ->
                                        ReviewItem(review)
                                        Spacer(Modifier.height(12.dp))
                                    }
                                }
                            }

                            Spacer(Modifier.height(8.dp))

                            Button(
                                onClick = vm::book,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Reserve Now", style = MaterialTheme.typography.titleLarge)
                                    Spacer(Modifier.weight(1f))
                                    Text("$${stay.nightlyPriceUsdEstimate * state.nights}", style = MaterialTheme.typography.titleLarge)
                                }
                            }
                            
                            Spacer(Modifier.height(32.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewItem(review: Review) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(review.author, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.weight(1f))
                repeat(review.rating.toInt()) {
                    Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFFFFB300))
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(review.text, style = MaterialTheme.typography.bodySmall, lineHeight = 18.sp)
        }
    }
}

@Composable
fun Counter(
    label: String,
    value: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Text(label, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(bottom = 4.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(4.dp)
        ) {
            IconButton(onClick = onDecrement) {
                Text("-", style = MaterialTheme.typography.titleLarge)
            }
            Text(value.toString(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            IconButton(onClick = onIncrement) {
                Text("+", style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}

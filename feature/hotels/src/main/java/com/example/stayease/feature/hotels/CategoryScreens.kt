package com.example.stayease.feature.hotels

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.stayease.domain.model.PointOfInterest
import com.example.stayease.domain.model.TravelEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelCategoryScreen(
    category: String,
    onBack: () -> Unit,
    viewModel: CategoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    LaunchedEffect(category) {
        viewModel.fetchCategoryData(category)
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                TopAppBar(
                    title = { Text(category, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.refresh() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                        }
                    }
                )
                SearchBar(
                    query = searchQuery,
                    onQueryChange = viewModel::onSearchQueryChanged,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.error != null) {
                Text(
                    text = "Error: ${uiState.error}",
                    modifier = Modifier.align(Alignment.Center).padding(16.dp),
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        CategoryHeader(category)
                    }
                    
                    if (uiState.items.isEmpty()) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text("No $category matches found in this area.", style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }

                    items(uiState.items) { item ->
                        when (item) {
                            is TravelEvent -> EventCard(item)
                            is PointOfInterest -> PoiCard(item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EventCard(event: TravelEvent) {
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp))
    ) {
        Column {
            Box(Modifier.height(160.dp).fillMaxWidth()) {
                AsyncImage(
                    model = event.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Surface(
                    modifier = Modifier.padding(12.dp).align(Alignment.TopEnd),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
                ) {
                    Text(
                        text = "LIVE EVENT",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = event.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Event, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(4.dp))
                    Text(text = event.eventDate ?: "Upcoming", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.width(16.dp))
                    Icon(Icons.Default.Place, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(4.dp))
                    Text(text = event.venueName ?: "Nearby", style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                
                // Fix: Capture external property in a local variable to allow smart cast
                val eventDescription = event.description
                if (!eventDescription.isNullOrBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = eventDescription,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { 
                        event.ticketUrl?.let { 
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it)))
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    enabled = event.ticketUrl != null
                ) {
                    Icon(Icons.Default.ConfirmationNumber, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Get Tickets")
                }
            }
        }
    }
}

@Composable
fun PoiCard(poi: PointOfInterest) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = poi.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(80.dp).clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surfaceVariant)
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = poi.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = poi.category.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (poi.rating != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = poi.rating.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            IconButton(onClick = { /* Add to favorites */ }) {
                Icon(Icons.Default.FavoriteBorder, contentDescription = "Favorite")
            }
        }
    }
}

@Composable
fun CategoryHeader(category: String) {
    val (description, icon, color) = when (category) {
        "Nature" -> Triple("Breathtaking landscapes and serene parks.", Icons.Default.Terrain, Color(0xFF2E7D32))
        "Music" -> Triple("Live gigs, concerts, and music festivals.", Icons.Default.MusicNote, Color(0xFFC2185B))
        "Events" -> Triple("Local exhibitions, fairs, and community gatherings.", Icons.Default.ConfirmationNumber, Color(0xFFF57C00))
        "Sports" -> Triple("Match days, stadiums, and active centers.", Icons.Default.SportsBasketball, Color(0xFF1976D2))
        "Social" -> Triple("Top cafes, bars, and social hotspots.", Icons.Default.Groups, Color(0xFF7B1FA2))
        else -> Triple("Explore the best spots in the city.", Icons.Default.Place, MaterialTheme.colorScheme.primary)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(48.dp), tint = color)
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    text = "Explore $category",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

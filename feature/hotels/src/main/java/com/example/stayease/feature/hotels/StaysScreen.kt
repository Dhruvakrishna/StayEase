package com.example.stayease.feature.hotels

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.example.stayease.domain.model.GeoPoint
import com.example.stayease.domain.model.Stay
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.math.absoluteValue

/**
 * Hey there! This is the main "Discovery" screen.
 * 
 * I've structured this as a mix of a featured carousel and a scrolling feed. 
 * From a UX perspective, it's a great way to show off "premium" content 
 * without burying the rest of the list.
 * 
 * Under the hood, we're leaning heavily on Paging 3. It's a bit of a setup, 
 * but it makes infinite scrolling feel butter-smooth on the device.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaysScreen(
    onStayClick: (Long) -> Unit,
    onMenuClick: () -> Unit,
    vm: StaysViewModel = hiltViewModel()
) {
    // We're grabbing the paging items here. The nice thing about this approach is that
    // the UI just "reacts" to whatever the repository sends over.
    val items = vm.stays.collectAsLazyPagingItems()
    val userLocation by vm.userLocation.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Location permissions can be annoying for users, so I've wrapped the request
    // in a friendly AlertDialog first. It's always better to ask nicely!
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            try {
                scope.launch {
                    // Using .await() here because it's way cleaner than nested callbacks.
                    val location = fusedLocationClient.lastLocation.await()
                    location?.let {
                        vm.updateLocation(GeoPoint(it.latitude, it.longitude))
                    }
                }
            } catch (e: SecurityException) {
                // If things go sideways (like a permission race condition), we catch it here.
            }
        }
    }

    var showLocationPopup by remember { mutableStateOf(userLocation == null) }

    if (showLocationPopup) {
        AlertDialog(
            onDismissRequest = { showLocationPopup = false },
            title = { Text("Find Stays Nearby?") },
            text = { Text("We use your location to show you the coolest spots in your current area.") },
            confirmButton = {
                TextButton(onClick = {
                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                    showLocationPopup = false
                }) { Text("Enable") }
            },
            dismissButton = {
                TextButton(onClick = { showLocationPopup = false }) { Text("Not Now") }
            }
        )
    }

    // Scaffold gives us that nice Material shell. I went with a CenterAlignedTopAppBar
    // because it feels a bit more "premium" and centered for a main landing screen.
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("StayEase", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "Open menu")
                    }
                },
                actions = {
                    IconButton(onClick = { showLocationPopup = true }) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Update location")
                    }
                }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            // Standard state handling: Loading, Error, or the actual List.
            when (items.loadState.refresh) {
                is LoadState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is LoadState.Error -> {
                    ErrorView(
                        message = "Looks like the internet is acting up. Give it another shot!",
                        onRetry = { items.retry() }
                    )
                }
                else -> {
                    // LazyColumn is our MVP here. It's super efficient with memory 
                    // since it only draws what you're actually looking at.
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // SECTION: The Hero Carousel
                        if (items.itemCount > 0) {
                            item {
                                Text(
                                    "Featured for You",
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    fontWeight = FontWeight.Bold
                                )
                                // Showing up to 5 featured items for a clean look.
                                val pagerState = rememberPagerState(pageCount = { Math.min(items.itemCount, 5) })
                                HorizontalPager(
                                    state = pagerState,
                                    contentPadding = PaddingValues(horizontal = 32.dp),
                                    pageSpacing = 16.dp,
                                    modifier = Modifier.height(280.dp)
                                ) { page ->
                                    val stay = items[page] ?: return@HorizontalPager
                                    
                                    // I added some 'pop' here using lerp for scale and alpha. 
                                    // It makes the swipe feel really organic as cards fade/shrink.
                                    FeaturedStayCard(stay, onClick = { onStayClick(stay.id) }, 
                                        modifier = Modifier.graphicsLayer {
                                            val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue
                                            val scale = lerp(0.85f, 1f, 1f - pageOffset.coerceIn(0f, 1f))
                                            scaleX = scale
                                            scaleY = scale
                                            alpha = lerp(0.6f, 1f, 1f - pageOffset.coerceIn(0f, 1f))
                                        }
                                    )
                                }
                            }
                        }

                        // SECTION: The Main Feed
                        item {
                            Text(
                                "All Available Stays",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        items(items.itemCount) { idx ->
                            val stay = items[idx] ?: return@items
                            StayListItem(stay, onClick = { onStayClick(stay.id) }, modifier = Modifier.padding(horizontal = 16.dp))
                        }

                        // This is the "loading more" indicator at the bottom for infinite scroll.
                        item {
                            if (items.loadState.append is LoadState.Loading) {
                                Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) { 
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp)) 
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * The Featured card is designed to be a "showstopper". 
 * Pro Tip: Always use a dark gradient overlay when placing text on images. 
 * You never know if the hotel photo will be pure white, and this saves the legibility!
 */
@Composable
fun FeaturedStayCard(stay: Stay, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxSize()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Box(Modifier.fillMaxSize()) {
            AsyncImage(
                // Using Unsplash as a fallback just in case the API doesn't have a thumbnail.
                model = stay.thumbnailUrl ?: "https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=800&q=80",
                contentDescription = "Image of ${stay.name}",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // The gradient starts about midway down to keep the bottom text readable.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                            startY = 400f
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(24.dp)
            ) {
                Text(
                    stay.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        stay.address ?: "Somewhere beautiful",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            // This is a little price tag badge. I went with primaryContainer color to make it pop.
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.padding(16.dp).align(Alignment.TopEnd)
            ) {
                Text(
                    "$$${stay.nightlyPriceUsdEstimate}",
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

/**
 * Standard list items. I went with a horizontal layout (image on left, info on right) 
 * because it's the most scanned pattern for users looking for quick details.
 */
@Composable
fun StayListItem(stay: Stay, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .height(110.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = stay.thumbnailUrl ?: "https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=200&q=80",
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(110.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
            )
            
            Spacer(Modifier.width(16.dp))
            
            Column(Modifier.weight(1f).fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(
                        stay.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        stay.address ?: "Address not listed",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "$$${stay.nightlyPriceUsdEstimate}/night",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.ExtraBold
                    )
                    
                    if (stay.rating != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(
                                String.format("%.1f", stay.rating),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Reusable error UI. Essential for mobile because, let's face it, connections drop all the time!
 */
@Composable
private fun ErrorView(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxWidth().padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            message, 
            style = MaterialTheme.typography.bodyLarge, 
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = onRetry,
            shape = RoundedCornerShape(12.dp)
        ) { 
            Text("Try Again") 
        }
    }
}

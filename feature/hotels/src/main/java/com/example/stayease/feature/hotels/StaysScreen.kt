package com.example.stayease.feature.hotels

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.example.stayease.domain.model.Stay
import com.example.stayease.core.nav.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaysScreen(
    onStayClick: (Long) -> Unit,
    onNavigate: (String) -> Unit,
    onMenuClick: () -> Unit,
    onSignInClick: () -> Unit,
    vm: StaysViewModel = hiltViewModel()
) {
    val items = vm.stays.collectAsLazyPagingItems()
    val searchQuery by vm.searchQuery.collectAsState()
    
    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                CenterAlignedTopAppBar(
                    title = { Text("StayEase", fontWeight = FontWeight.ExtraBold) },
                    navigationIcon = {
                        IconButton(onClick = onMenuClick) {
                            Icon(Icons.Default.Menu, contentDescription = "Open menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = onSignInClick) {
                            Icon(Icons.Default.AccountCircle, contentDescription = "Sign In")
                        }
                    }
                )
                SearchBar(
                    query = searchQuery,
                    onQueryChange = vm::onSearchQueryChanged,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // TRAVEL COMPANION: Type of Travel Navigation
                item {
                    Text(
                        "Discover Your Vibe",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(16.dp))
                    TravelTypeRow(onNavigate)
                }

                if (items.loadState.refresh is LoadState.Loading) {
                    item {
                        Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }

                // Featured Content
                if (items.itemCount > 0 && searchQuery.isEmpty()) {
                    item {
                        Text(
                            "Top Destinations",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(horizontal = 16.dp),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(12.dp))
                        val pagerState = rememberPagerState(pageCount = { Math.min(items.itemCount, 5) })
                        HorizontalPager(
                            state = pagerState,
                            contentPadding = PaddingValues(horizontal = 32.dp),
                            pageSpacing = 16.dp,
                            modifier = Modifier.height(260.dp)
                        ) { page ->
                            val stay = items[page] ?: return@HorizontalPager
                            FeaturedStayCard(stay, onClick = { onStayClick(stay.id) })
                        }
                    }
                }

                // Main List
                item {
                    Text(
                        "All Adventures",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        fontWeight = FontWeight.Bold
                    )
                }

                items(items.itemCount) { idx ->
                    val stay = items[idx] ?: return@items
                    StayListItem(stay, onClick = { onStayClick(stay.id) }, modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }
    }
}

@Composable
fun TravelTypeRow(onNavigate: (String) -> Unit) {
    val travelTypes = listOf(
        TravelTypeItem("Nature", Icons.Default.Terrain, Routes.TRAVEL_NATURE, Color(0xFF2E7D32)),
        TravelTypeItem("Music", Icons.Default.MusicNote, Routes.TRAVEL_MUSIC, Color(0xFFC2185B)),
        TravelTypeItem("Events", Icons.Default.ConfirmationNumber, Routes.TRAVEL_EVENTS, Color(0xFFF57C00)),
        TravelTypeItem("Sports", Icons.Default.SportsBasketball, Routes.TRAVEL_SPORTS, Color(0xFF1976D2)),
        TravelTypeItem("Social", Icons.Default.Groups, Routes.TRAVEL_SOCIAL, Color(0xFF7B1FA2))
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        items(travelTypes) { type ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onNavigate(type.route) }
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = type.color.copy(alpha = 0.15f),
                    modifier = Modifier.size(72.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(type.icon, contentDescription = type.name, tint = type.color, modifier = Modifier.size(32.dp))
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(type.name, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            }
        }
    }
}

data class TravelTypeItem(val name: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val route: String, val color: Color)

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text("Search your next getaway...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        shape = RoundedCornerShape(28.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    )
}

@Composable
fun FeaturedStayCard(stay: Stay, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxSize()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(Modifier.fillMaxSize()) {
            AsyncImage(
                model = stay.thumbnailUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                            startY = 350f
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(20.dp)
            ) {
                Text(stay.name, style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.ExtraBold)
                Text(stay.category, style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.7f))
            }
        }
    }
}

@Composable
fun StayListItem(stay: Stay, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = stay.thumbnailUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(90.dp).clip(RoundedCornerShape(20.dp))
            )
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(stay.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1)
                Text(stay.address ?: "Adventure awaits", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("$$${stay.nightlyPriceUsdEstimate}", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.weight(1f))
                    if (stay.rating != null) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp))
                        Text(stay.rating.toString(), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

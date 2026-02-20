package com.example.stayease.feature.hotels

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AITravelAssistantScreen(
    onBack: () -> Unit,
    viewModel: AITravelViewModel = hiltViewModel()
) {
    var query by remember { mutableStateOf("") }
    val chatHistory = viewModel.chatHistory
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Travel Assistant", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(chatHistory) { (message, isUser) ->
                    ChatBubble(message, isUser)
                }
                if (uiState.isAiLoading) {
                    item {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp).align(Alignment.CenterHorizontally))
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Ask about destinations, hotels...") },
                    shape = MaterialTheme.shapes.medium
                )
                Spacer(Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (query.isNotBlank()) {
                            viewModel.sendMessage(query)
                            query = ""
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = "Send", tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MLRecommendationsScreen(
    onBack: () -> Unit,
    viewModel: AITravelViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Smart Recommendations", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isRecommendationsLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        "AI Powered Trip Suggestions",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "Based on your current location and travel patterns.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                if (uiState.recommendations.isEmpty()) {
                    item {
                        Text("No specific recommendations found nearby. Try exploring a new area!")
                    }
                }

                items(uiState.recommendations) { poi ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Place, contentDescription = null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.secondary)
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text(poi.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text(poi.category, style = MaterialTheme.typography.bodyMedium)
                                if (poi.rating != null) {
                                    Text("Rating: ${poi.rating} ⭐", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: String, isUser: Boolean) {
    val alignment = if (isUser) Alignment.End else Alignment.Start
    val containerColor = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = alignment) {
        Surface(
            color = containerColor,
            contentColor = contentColor,
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = message,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

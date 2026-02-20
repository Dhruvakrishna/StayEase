package com.example.stayease.feature.hotels

import android.graphics.Color
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.stayease.domain.model.GeoPoint
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint as OsmGeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapSearchScreen(
    onBack: () -> Unit,
    onStayClick: (Long) -> Unit,
    vm: StaysViewModel = hiltViewModel()
) {
    val items = vm.stays.collectAsLazyPagingItems()
    val userLocation by vm.userLocation.collectAsState()
    val radius by vm.radius.collectAsState()
    val context = LocalContext.current

    // It's a good practice to configure osmdroid with a user agent and a cache directory.
    // This is done once when the composable enters the composition.
    LaunchedEffect(Unit) {
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", 0))
    }

    // Remember the MapView instance to be able to control it from outside the AndroidView.
    val mapView = remember {
        MapView(context)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Explore Nearby") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            AndroidView(
                factory = {
                    mapView.apply {
                        setMultiTouchControls(true)
                        controller.setZoom(13.0)
                        val startPoint = userLocation?.let { OsmGeoPoint(it.lat, it.lon) }
                            ?: OsmGeoPoint(41.8781, -87.6298) // Default to Chicago
                        controller.setCenter(startPoint)
                    }
                },
                update = { view ->
                    // This update block is called on recompositions.
                    // We clear only the overlays we manage (markers and the radius circle).
                    view.overlays.removeAll { it is Marker || it is Polygon }

                    // Add a circle to visualize the search radius.
                    val circle = Polygon().apply {
                        val center = view.mapCenter
                        val osmCenter = OsmGeoPoint(center.latitude, center.longitude)
                        points = Polygon.pointsAsCircle(osmCenter, radius.toDouble())
                        @Suppress("DEPRECATION")
                        fillPaint.color = 0x200000FF // Blue with some transparency
                        @Suppress("DEPRECATION")
                        outlinePaint.color = Color.TRANSPARENT
                    }
                    view.overlays.add(0, circle) // Add at the bottom of the stack

                    // Add markers for each stay.
                    for (i in 0 until items.itemCount) {
                        val stay = items[i] ?: continue
                        val marker = Marker(view).apply {
                            position = OsmGeoPoint(stay.location.lat, stay.location.lon)
                            title = stay.name
                            subDescription = "$${stay.nightlyPriceUsdEstimate} / night"
                            setOnMarkerClickListener { m, _ ->
                                m.showInfoWindow()
                                true
                            }
                            infoWindow.view.setOnClickListener {
                                onStayClick(stay.id)
                            }
                        }
                        view.overlays.add(marker)
                    }
                    view.invalidate()
                },
                modifier = Modifier.fillMaxSize()
            )

            ExtendedFloatingActionButton(
                onClick = {
                    // When the user clicks this button, we take the current center of the map
                    // and trigger a new search for stays in that area.
                    val center = mapView.mapCenter
                    vm.updateLocation(GeoPoint(center.latitude, center.longitude))
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                icon = { Icon(Icons.Default.Refresh, contentDescription = null) },
                text = { Text("Search this area") }
            )
        }
    }
}

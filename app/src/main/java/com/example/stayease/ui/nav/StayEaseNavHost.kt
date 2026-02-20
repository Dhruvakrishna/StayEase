package com.example.stayease.ui.nav

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.stayease.feature.asyncdemo.TripPlansScreen
import com.example.stayease.feature.auth.*
import com.example.stayease.feature.bookings.BookingsScreen
import com.example.stayease.feature.details.StayDetailsScreen
import com.example.stayease.feature.hotels.*
import com.example.stayease.core.nav.Routes
import kotlinx.coroutines.launch

@Composable
fun StayEaseNavHost(vm: SessionViewModel = hiltViewModel()) {
    val nav = rememberNavController()
    val loggedIn by vm.loggedIn.collectAsState()
    val isOnline by vm.isOnline.collectAsState()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(isOnline) {
        if (!isOnline) {
            snackbarHostState.showSnackbar(
                message = "You are currently offline. Some features may be unavailable.",
                duration = SnackbarDuration.Indefinite
            )
        } else {
            snackbarHostState.currentSnackbarData?.dismiss()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("StayEase", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
                HorizontalDivider()
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Explore, contentDescription = null) },
                    label = { Text("Explore Destinations") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        nav.navigate(Routes.STAYS) {
                            popUpTo(Routes.STAYS) { inclusive = true }
                        }
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.ConfirmationNumber, contentDescription = null) },
                    label = { Text("My Bookings") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        nav.navigate(Routes.BOOKINGS)
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.AutoAwesome, contentDescription = null) },
                    label = { Text("AI Assistant") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        nav.navigate(Routes.TRAVEL_AI_ASSISTANT)
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Lightbulb, contentDescription = null) },
                    label = { Text("Smart Recommendations") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        nav.navigate(Routes.TRAVEL_RECOMMENDATIONS)
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text("Settings") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        nav.navigate(Routes.SETTINGS)
                    }
                )
                Spacer(Modifier.weight(1f))
                HorizontalDivider()
                if(loggedIn){
                     NavigationDrawerItem(
                        label = { Text( "Sign Out" ) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            vm.logoutNow()
                        }
                    )
                }
            }
        }
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
            Box(Modifier.padding(padding)) {
                NavHost(navController = nav, startDestination = Routes.STAYS) {
                    composable(Routes.AUTH) {
                        LoginScreen(onLoggedIn = { nav.navigate(Routes.STAYS) { popUpTo(Routes.AUTH) { inclusive = true } } })
                    }
                    composable(Routes.STAYS) {
                        StaysScreen(
                            onStayClick = { id -> nav.navigate("${Routes.DETAILS}/$id") },
                            onNavigate = { route -> nav.navigate(route) },
                            onMenuClick = { scope.launch { drawerState.open() } },
                            onSignInClick = { nav.navigate(Routes.AUTH) }
                        )
                    }
                    composable("${Routes.DETAILS}/{id}", arguments = listOf(navArgument("id") { type = NavType.LongType })) {
                        StayDetailsScreen(onBack = { nav.popBackStack() }, onBookings = { nav.navigate(Routes.BOOKINGS) })
                    }
                    composable(Routes.BOOKINGS) { BookingsScreen(onBack = { nav.popBackStack() }) }
                    composable(Routes.PROFILE) { ProfileScreen(onBack = { nav.popBackStack() }) }
                    composable(Routes.SETTINGS) { SettingsScreen(onBack = { nav.popBackStack() }) }
                    composable(Routes.FAVORITES) { FavoritesScreen(onBack = { nav.popBackStack() }, onStayClick = { id -> nav.navigate("${Routes.DETAILS}/$id") }) }
                    composable(Routes.SUPPORT) { SupportScreen(onBack = { nav.popBackStack() }) }
                    composable(Routes.INBOX) { InboxScreen(onBack = { nav.popBackStack() }) }
                    composable(Routes.WALLET) { WalletScreen(onBack = { nav.popBackStack() }) }
                    composable(Routes.NOTIFICATIONS) { NotificationsScreen(onBack = { nav.popBackStack() }) }
                    composable(Routes.MAP) { MapSearchScreen(onBack = { nav.popBackStack() }, onStayClick = { id -> nav.navigate("${Routes.DETAILS}/$id") }) }
                    composable(Routes.TRIPS) { TripPlansScreen(onBack = { nav.popBackStack() }) }

                    composable(Routes.TRAVEL_AI_ASSISTANT) { AITravelAssistantScreen(onBack = { nav.popBackStack() }) }
                    composable(Routes.TRAVEL_RECOMMENDATIONS) { MLRecommendationsScreen(onBack = { nav.popBackStack() }) }

                    composable(Routes.TRAVEL_NATURE) { TravelCategoryScreen(category = "Nature", onBack = { nav.popBackStack() }) }
                    composable(Routes.TRAVEL_MUSIC) { TravelCategoryScreen(category = "Music", onBack = { nav.popBackStack() }) }
                    composable(Routes.TRAVEL_EVENTS) { TravelCategoryScreen(category = "Events", onBack = { nav.popBackStack() }) }
                    composable(Routes.TRAVEL_SPORTS) { TravelCategoryScreen(category = "Sports", onBack = { nav.popBackStack() }) }
                    composable(Routes.TRAVEL_SOCIAL) { TravelCategoryScreen(category = "Social", onBack = { nav.popBackStack() }) }
                }
            }
        }
    }
}

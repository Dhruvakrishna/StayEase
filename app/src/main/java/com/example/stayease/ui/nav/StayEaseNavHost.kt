package com.example.stayease.ui.nav

import androidx.compose.foundation.layout.*
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
import kotlinx.coroutines.launch

@Composable
fun StayEaseNavHost(vm: SessionViewModel = hiltViewModel()) {
    val nav = rememberNavController()
    val loggedIn by vm.loggedIn.collectAsState()
    val settings by vm.settings.collectAsState()
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

    if (!settings.hasCompletedOnboarding) {
        OnboardingScreen(onFinished = { vm.completeOnboarding() })
    } else {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    Text("StayEase", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
                    HorizontalDivider()
                    NavigationDrawerItem(
                        label = { Text("Home") },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            nav.navigate(Routes.STAYS) {
                                popUpTo(Routes.STAYS) { inclusive = true }
                            }
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text("Explore") },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            nav.navigate(Routes.MAP)
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text("Inbox") },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            nav.navigate(Routes.INBOX)
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text("Wallet") },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            nav.navigate(Routes.WALLET)
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text("My Favorites") },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            nav.navigate(Routes.FAVORITES)
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text("My Bookings") },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            nav.navigate(Routes.BOOKINGS)
                        }
                    )
                     NavigationDrawerItem(
                        label = { Text("Trip Plans") },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            nav.navigate(Routes.TRIPS)
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text("Profile") },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            nav.navigate(Routes.PROFILE)
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text("Support") },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            nav.navigate(Routes.SUPPORT)
                        }
                    )
                    NavigationDrawerItem(
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
                    }
                }
            }
        }
    }
}

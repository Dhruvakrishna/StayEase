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
import com.example.stayease.feature.auth.LoginScreen
import com.example.stayease.feature.bookings.BookingsScreen
import com.example.stayease.feature.details.StayDetailsScreen
import com.example.stayease.feature.hotels.StaysScreen
import kotlinx.coroutines.launch

@Composable
fun StayEaseNavHost(vm: SessionViewModel = hiltViewModel()) {
  val nav = rememberNavController()
  val loggedIn by vm.loggedIn.collectAsState(initial = false)
  val drawerState = rememberDrawerState(DrawerValue.Closed)
  val scope = rememberCoroutineScope()

  ModalNavigationDrawer(
    drawerState = drawerState,
    drawerContent = {
      ModalDrawerSheet {
        Text("Stays", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
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
          label = { Text("My Bookings") },
          selected = false,
          onClick = {
            scope.launch { drawerState.close() }
            nav.navigate(Routes.BOOKINGS)
          }
        )
        NavigationDrawerItem(
          label = { Text("Settings") },
          selected = false,
          onClick = {
            scope.launch { drawerState.close() }
            // nav.navigate(Routes.SETTINGS) // Placeholder
          }
        )
        Spacer(Modifier.weight(1f))
        HorizontalDivider()
        NavigationDrawerItem(
          label = { Text(if (loggedIn) "Sign Out" else "Sign In") },
          selected = false,
          onClick = {
            scope.launch { drawerState.close() }
            if (loggedIn) {
              vm.logoutNow()
            } else {
              nav.navigate(Routes.AUTH)
            }
          }
        )
      }
    }
  ) {
    NavHost(navController = nav, startDestination = Routes.STAYS) {
      composable(Routes.AUTH) {
        LoginScreen(onLoggedIn = { nav.navigate(Routes.STAYS) { popUpTo(Routes.AUTH) { inclusive = true } } })
      }
      composable(Routes.STAYS) {
        StaysScreen(
          onStayClick = { id -> nav.navigate("${Routes.DETAILS}/$id") },
          onMenuClick = { scope.launch { drawerState.open() } }
        )
      }
      composable("${Routes.DETAILS}/{id}", arguments = listOf(navArgument("id") { type = NavType.LongType })) {
        StayDetailsScreen(onBack = { nav.popBackStack() }, onBookings = { nav.navigate(Routes.BOOKINGS) })
      }
      composable(Routes.BOOKINGS) { BookingsScreen(onBack = { nav.popBackStack() }) }
    }
  }
}

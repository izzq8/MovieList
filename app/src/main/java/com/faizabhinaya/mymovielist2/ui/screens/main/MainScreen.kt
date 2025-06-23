package com.faizabhinaya.mymovielist2.ui.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.faizabhinaya.mymovielist2.ui.navigation.Screen
import com.faizabhinaya.mymovielist2.ui.screens.home.HomeScreen
import com.faizabhinaya.mymovielist2.ui.screens.profile.ProfileScreen
import com.faizabhinaya.mymovielist2.ui.screens.search.SearchScreen
import com.faizabhinaya.mymovielist2.ui.screens.watchlist.WatchlistScreen

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: @Composable () -> Unit,
    val unselectedIcon: @Composable () -> Unit
) {
    object Home : BottomNavItem(
        route = "home",
        title = "Home",
        selectedIcon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
        unselectedIcon = { Icon(Icons.Outlined.Home, contentDescription = "Home") }
    )

    object Search : BottomNavItem(
        route = "search",
        title = "Search",
        selectedIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
        unselectedIcon = { Icon(Icons.Outlined.Search, contentDescription = "Search") }
    )

    object Watchlist : BottomNavItem(
        route = "watchlist",
        title = "Watchlist",
        selectedIcon = { Icon(Icons.Filled.Favorite, contentDescription = "Watchlist") },
        unselectedIcon = { Icon(Icons.Outlined.FavoriteBorder, contentDescription = "Watchlist") }
    )

    object Profile : BottomNavItem(
        route = "profile",
        title = "Profile",
        selectedIcon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
        unselectedIcon = { Icon(Icons.Outlined.Person, contentDescription = "Profile") }
    )
}

@Composable
fun MainScreen(
    onNavigateToMovieDetail: (Int) -> Unit,
    navController: NavHostController
) {
    val bottomNavController = rememberNavController()
    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Search,
        BottomNavItem.Watchlist,
        BottomNavItem.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { item ->
                    val selected = currentDestination?.hierarchy?.any {
                        it.route == item.route
                    } == true

                    NavigationBarItem(
                        icon = { if (selected) item.selectedIcon() else item.unselectedIcon() },
                        label = { Text(item.title) },
                        selected = selected,
                        onClick = {
                            bottomNavController.navigate(item.route) {
                                popUpTo(bottomNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = bottomNavController,
                startDestination = BottomNavItem.Home.route
            ) {
                composable(BottomNavItem.Home.route) {
                    HomeScreen(onNavigateToMovieDetail = onNavigateToMovieDetail)
                }
                composable(BottomNavItem.Search.route) {
                    SearchScreen(onNavigateToMovieDetail = onNavigateToMovieDetail)
                }
                composable(BottomNavItem.Watchlist.route) {
                    WatchlistScreen(onNavigateToMovieDetail = onNavigateToMovieDetail)
                }
                composable(BottomNavItem.Profile.route) {
                    // Menggunakan navController dari AppNavHost untuk navigasi logout
                    ProfileScreen(navController = navController)
                }
            }
        }
    }
}

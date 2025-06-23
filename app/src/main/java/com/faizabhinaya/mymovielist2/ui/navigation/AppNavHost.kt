package com.faizabhinaya.mymovielist2.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.faizabhinaya.mymovielist2.ui.screens.actor.ActorDetailScreen
import com.faizabhinaya.mymovielist2.ui.screens.auth.ForgotPasswordScreen
import com.faizabhinaya.mymovielist2.ui.screens.auth.SignInScreen
import com.faizabhinaya.mymovielist2.ui.screens.auth.SignUpScreen
import com.faizabhinaya.mymovielist2.ui.screens.detail.MovieDetailScreen
import com.faizabhinaya.mymovielist2.ui.screens.home.HomeScreen
import com.faizabhinaya.mymovielist2.ui.screens.main.MainScreen
import com.faizabhinaya.mymovielist2.ui.screens.profile.ProfileScreen
import com.faizabhinaya.mymovielist2.ui.screens.search.SearchScreen
import com.faizabhinaya.mymovielist2.ui.screens.splash.SplashScreen
import com.faizabhinaya.mymovielist2.ui.screens.watchlist.WatchlistScreen

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object SignIn : Screen("signIn")
    object SignUp : Screen("signUp")
    object ForgotPassword : Screen("forgotPassword")
    object Main : Screen("main")
    object Home : Screen("home")
    object Search : Screen("search")
    object Watchlist : Screen("watchlist")
    object Profile : Screen("profile")
    object MovieDetail : Screen("movieDetail/{movieId}") {
        fun createRoute(movieId: Int) = "movieDetail/$movieId"
    }
    object ActorDetail : Screen("actorDetail/{actorId}") {
        fun createRoute(actorId: Int) = "actorDetail/$actorId"
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToSignIn = { navController.navigate(Screen.SignIn.route) },
                onNavigateToMain = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.SignIn.route) {
            SignInScreen(
                onNavigateToSignUp = { navController.navigate(Screen.SignUp.route) },
                onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) },
                onSignInSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.SignIn.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(
                onNavigateToSignIn = { navController.navigate(Screen.SignIn.route) {
                    popUpTo(Screen.SignUp.route) { inclusive = true }
                }},
                onSignUpSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.SignUp.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(Screen.Main.route) {
            MainScreen(
                onNavigateToMovieDetail = { movieId ->
                    navController.navigate(Screen.MovieDetail.createRoute(movieId))
                },
                navController = navController
            )
        }

        composable(
            route = Screen.MovieDetail.route,
            arguments = listOf(
                navArgument("movieId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt("movieId") ?: return@composable
            MovieDetailScreen(
                movieId = movieId,
                onNavigateBack = { navController.navigateUp() },
                onActorClick = { actorId ->
                    navController.navigate(Screen.ActorDetail.createRoute(actorId))
                }
            )
        }

        composable(
            route = Screen.ActorDetail.route,
            arguments = listOf(
                navArgument("actorId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val actorId = backStackEntry.arguments?.getInt("actorId") ?: return@composable
            ActorDetailScreen(
                actorId = actorId,
                onNavigateBack = { navController.navigateUp() },
                onMovieClick = { movieId ->
                    navController.navigate(Screen.MovieDetail.createRoute(movieId))
                }
            )
        }
    }
}

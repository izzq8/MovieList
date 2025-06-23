package com.faizabhinaya.mymovielist2.ui.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.faizabhinaya.mymovielist2.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToSignIn: () -> Unit,
    onNavigateToMain: () -> Unit,
    viewModel: SplashViewModel = viewModel()
) {
    val isUserLoggedIn by viewModel.isUserLoggedIn.collectAsState()
    val isCheckingAuth by viewModel.isCheckingAuth.collectAsState()

    LaunchedEffect(key1 = isUserLoggedIn) {
        delay(2000) // Display splash screen for 2 seconds
        if (isCheckingAuth) return@LaunchedEffect

        if (isUserLoggedIn) {
            onNavigateToMain()
        } else {
            onNavigateToSignIn()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Replace with your app logo
            Image(
                painter = painterResource(id = R.drawable.movielistlogo),
                contentDescription = "App Logo",
                modifier = Modifier.size(150.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "myMovieList",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

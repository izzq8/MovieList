package com.faizabhinaya.mymovielist2.ui.screens.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.faizabhinaya.mymovielist2.data.model.MovieReview
import com.faizabhinaya.mymovielist2.data.repository.FirebaseReviewRepository
import com.faizabhinaya.mymovielist2.ui.viewmodel.ReviewViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Composable
fun DebugReviewScreen(movieId: Int) {
    val reviewViewModel: ReviewViewModel = viewModel()
    val repository = FirebaseReviewRepository()
    val scope = rememberCoroutineScope()
    var rawDocuments by remember { mutableStateOf<List<String>>(emptyList()) }
    var debugMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(movieId) {
        // Normal way to get reviews through ViewModel
        reviewViewModel.getAllReviewsByMovieId(movieId)

        // Debug way to check raw data in Firestore
        isLoading = true
        try {
            val result = repository.debugGetAllReviewsRaw(movieId)
            rawDocuments = result
            debugMessage = "Found ${result.size} raw documents"
            isLoading = false
        } catch (e: Exception) {
            debugMessage = "Error: ${e.message}"
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text("Current User ID: ${FirebaseAuth.getInstance().currentUser?.uid ?: "Not logged in"}")
        Text("Current User Name: ${FirebaseAuth.getInstance().currentUser?.displayName ?: "Unknown"}")

        Spacer(modifier = Modifier.height(16.dp))

        Text("Debug Information:")
        Text(debugMessage)

        Spacer(modifier = Modifier.height(16.dp))

        Text("Raw Firestore Documents (${rawDocuments.size}):")

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            LazyColumn {
                items(rawDocuments) { doc ->
                    Text(doc)
                    Divider()
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            scope.launch {
                try {
                    isLoading = true
                    val result = repository.debugGetAllReviewsRaw(movieId)
                    rawDocuments = result
                    debugMessage = "Found ${result.size} raw documents"
                    isLoading = false
                } catch (e: Exception) {
                    debugMessage = "Error: ${e.message}"
                    isLoading = false
                }
            }
        }) {
            Text("Refresh Raw Data")
        }
    }
}

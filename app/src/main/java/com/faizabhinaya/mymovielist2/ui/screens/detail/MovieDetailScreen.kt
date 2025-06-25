package com.faizabhinaya.mymovielist2.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.faizabhinaya.mymovielist2.R
import com.faizabhinaya.mymovielist2.data.model.Cast
import com.faizabhinaya.mymovielist2.data.model.Genre
import com.faizabhinaya.mymovielist2.utils.Constants
import com.faizabhinaya.mymovielist2.ui.viewmodel.ReviewViewModel
import com.faizabhinaya.mymovielist2.data.model.MovieReview
import com.faizabhinaya.mymovielist2.ui.components.MovieListStyleDialog
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    movieId: Int,
    onNavigateBack: () -> Unit,
    onActorClick: (Int) -> Unit = {},
    viewModel: MovieDetailViewModel = viewModel(factory = MovieDetailViewModel.Factory(movieId))
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Review ViewModel
    val reviewViewModel: ReviewViewModel = viewModel()
    val reviewUiState by reviewViewModel.uiState.collectAsStateWithLifecycle()

    // FIXED: Dialog state dengan nama yang konsisten
    var showMovieListDialog by remember { mutableStateOf(false) }

    // Load review untuk movie ini
    LaunchedEffect(movieId) {
        reviewViewModel.getReviewByMovieId(movieId)
        reviewViewModel.getAllReviewsByMovieId(movieId)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(uiState.movieDetails?.title ?: "") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    if (!uiState.isLoading && uiState.movieDetails != null) {
                        IconButton(onClick = {
                            if (uiState.isInWatchlist) {
                                viewModel.removeFromWatchlist()
                            } else {
                                viewModel.addToWatchlist()
                            }
                        }) {
                            Icon(
                                imageVector = if (uiState.isInWatchlist) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = if (uiState.isInWatchlist)
                                    stringResource(R.string.remove_from_watchlist) else
                                    stringResource(R.string.add_to_watchlist)
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.error != null) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error: ${uiState.error}",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadMovieDetails() }) {
                            Text("Retry")
                        }
                    }
                }
            } else {
                uiState.movieDetails?.let { movieDetails ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Movie backdrop
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(Constants.IMAGE_BASE_URL + (movieDetails.backdropPath ?: movieDetails.posterPath))
                                    .crossfade(true)
                                    .build(),
                                contentDescription = movieDetails.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            // Overlay supaya better text visibility
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.5f))
                            )

                            // Rating badge
                            Box(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .align(Alignment.TopEnd)
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        Icons.Default.Star,
                                        contentDescription = "Rating",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = String.format("%.1f", movieDetails.rating),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }

                        // Movie details
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = movieDetails.title,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Release year & runtime
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                movieDetails.releaseDate?.let { releaseDate ->
                                    if (releaseDate.length >= 4) {
                                        Text(
                                            text = releaseDate.substring(0, 4),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                movieDetails.runtime?.let { runtime ->
                                    Text(
                                        text = "$runtime min",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Genres
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                movieDetails.genres?.take(3)?.forEach { genre ->
                                    GenreChip(genre = genre)
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Overview
                            Text(
                                text = "Overview",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = movieDetails.overview ?: "No overview available",
                                style = MaterialTheme.typography.bodyLarge
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Cast
                            Text(
                                text = "Cast",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            if (movieDetails.credits?.cast.isNullOrEmpty()) {
                                Text(
                                    text = "Cast information not available",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            } else {
                                LazyRow(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(movieDetails.credits?.cast?.take(20) ?: emptyList()) { cast ->
                                        CastItem(cast = cast, onActorClick = onActorClick)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // ENHANCED Review Section
                            Text(
                                text = "Your Review",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // ENHANCED Review Card w/ better styling
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showMovieListDialog = true },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (reviewUiState.currentReview != null) {
                                        MaterialTheme.colorScheme.primaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant
                                    }
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    if (reviewUiState.currentReview != null) {
                                        // Show existing review
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Rating stars
                                            Row {
                                                repeat(5) { index ->
                                                    Icon(
                                                        imageVector = Icons.Default.Star,
                                                        contentDescription = null,
                                                        tint = if (index < (reviewUiState.currentReview?.rating?.toInt() ?: 0)) {
                                                            Color(0xFFFFD700) // Gold
                                                        } else {
                                                            Color.Gray.copy(alpha = 0.3f)
                                                        },
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.width(8.dp))

                                            Text(
                                                text = "${reviewUiState.currentReview?.rating?.toInt()}/5",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }

                                        if (!reviewUiState.currentReview?.review.isNullOrBlank()) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = reviewUiState.currentReview?.review ?: "",
                                                style = MaterialTheme.typography.bodyMedium,
                                                maxLines = 3,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Tap to edit your review",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    } else {
                                        // No review yet - ENHANCED styling
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Empty stars preview
                                            Row {
                                                repeat(5) {
                                                    Icon(
                                                        imageVector = Icons.Default.Star,
                                                        contentDescription = null,
                                                        tint = Color.Gray.copy(alpha = 0.3f),
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.width(12.dp))

                                            Column {
                                                Text(
                                                    text = "Add a review",
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    fontWeight = FontWeight.Medium
                                                )
                                                Text(
                                                    text = "Hey cinephile, so what do ya think?",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(32.dp))

                            // All Reviews Section
                            Text(
                                text = "All Reviews",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Display all reviews or loading state
                            if (reviewUiState.isLoadingAllReviews) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            } else if (reviewUiState.allMovieReviews.isEmpty()) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(32.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "No reviews yet. Be the first to review!",
                                            style = MaterialTheme.typography.bodyLarge,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            } else {
                                // Display the reviews
                                reviewUiState.allMovieReviews.forEach { review ->
                                    com.faizabhinaya.mymovielist2.ui.components.ReviewItem(
                                        review = review,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }
            }
        }
    }

    // ENHANCED Dialog dengan delete functionality
    if (showMovieListDialog) {
        MovieListStyleDialog(
            movieId = movieId,
            movieTitle = uiState.movieDetails?.title ?: "",
            existingReview = reviewUiState.currentReview,
            onDismiss = { showMovieListDialog = false },
            onSaveReview = { review ->
                reviewViewModel.saveReview(review)
            },
            onDeleteReview = {
                reviewUiState.currentReview?.let { review ->
                    reviewViewModel.deleteReview(review.id)
                }
            }
        )
    }

    // Show snackbar messages for watchlist actions
    LaunchedEffect(uiState.watchlistActionMessage) {
        uiState.watchlistActionMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearWatchlistActionMessage()
        }
    }

    // Show snackbar messages for review actions
    LaunchedEffect(reviewUiState.successMessage, reviewUiState.errorMessage) {
        reviewUiState.successMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            reviewViewModel.clearMessages()
        }
        reviewUiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            reviewViewModel.clearMessages()
        }
    }
}

@Composable
fun GenreChip(genre: Genre) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    ) {
        Text(
            text = genre.name,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun CastItem(cast: Cast, onActorClick: (Int) -> Unit = {}) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(80.dp)
            .clickable { onActorClick(cast.id) }
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(Constants.IMAGE_BASE_URL + cast.profilePath)
                .crossfade(true)
                .build(),
            contentDescription = cast.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = cast.name,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = cast.character,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

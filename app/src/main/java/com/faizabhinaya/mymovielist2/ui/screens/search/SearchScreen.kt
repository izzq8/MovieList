package com.faizabhinaya.mymovielist2.ui.screens.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.faizabhinaya.mymovielist2.data.model.Movie
import com.faizabhinaya.mymovielist2.utils.Constants
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onNavigateToMovieDetail: (Int) -> Unit,
    viewModel: SearchViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var history by remember { mutableStateOf(listOf<String>()) }

    fun refreshHistory() {
        coroutineScope.launch {
            history = SearchHistoryFirestoreManager.getHistory()
        }
    }

    LaunchedEffect(Unit) {
        refreshHistory()
    }

    var isSearchActive by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Top,
            ) {
                TopAppBar(
                    title = {
                        Text(
                            text = "Search Movies",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    ),
                    windowInsets = WindowInsets(0),
                    modifier = Modifier.padding(bottom = 0.dp)
                )
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = {
                        viewModel.searchMovies(it)
                        coroutineScope.launch {
                            SearchHistoryFirestoreManager.saveQuery(it)
                            refreshHistory()
                        }
                        isSearchActive = false
                    },
                    active = isSearchActive,
                    onActiveChange = { isSearchActive = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-32).dp)
                        .padding(horizontal = 8.dp, vertical = 0.dp),
                    placeholder = { Text("Search for movies...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = {
                                searchQuery = ""
                                viewModel.clearSearch()
                            }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    }
                ) {
                    if (isSearchActive && searchQuery.isEmpty() && history.isNotEmpty()) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Recent Searches",
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.padding(8.dp)
                            )
                            history.forEach { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            searchQuery = item
                                            viewModel.searchMovies(item)
                                            coroutineScope.launch {
                                                SearchHistoryFirestoreManager.saveQuery(item)
                                                refreshHistory()
                                            }
                                            isSearchActive = false
                                        }
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Search, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = item,
                                        modifier = Modifier.weight(1f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    IconButton(onClick = {
                                        coroutineScope.launch {
                                            SearchHistoryFirestoreManager.removeQuery(item)
                                            refreshHistory()
                                        }
                                    }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Remove")
                                    }
                                }
                            }
                            if (history.isNotEmpty()) {
                                TextButton(onClick = {
                                    coroutineScope.launch {
                                        SearchHistoryFirestoreManager.clearHistory()
                                        refreshHistory()
                                    }
                                }, modifier = Modifier.align(Alignment.End)) {
                                    Text("Clear All")
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Konten hasil pencarian
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
                    Text(
                        text = uiState.error ?: "An error occurred",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            } else if (uiState.movies.isEmpty() && !uiState.isInitialState) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "No movies found",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else if (uiState.isInitialState) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "Search for your favorite movies",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.movies) { movie ->
                        SearchResultItem(
                            movie = movie,
                            onMovieClick = { onNavigateToMovieDetail(movie.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResultItem(
    movie: Movie,
    onMovieClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onMovieClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(Constants.IMAGE_BASE_URL + movie.posterPath)
                    .crossfade(true)
                    .build(),
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(80.dp)
                    .fillMaxHeight()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                movie.releaseDate?.let { releaseDate ->
                    if (releaseDate.isNotEmpty() && releaseDate.length >= 4) {
                        Text(
                            text = "Released: ${releaseDate.substring(0, 4)}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "⭐ ${String.format("%.1f", movie.rating)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

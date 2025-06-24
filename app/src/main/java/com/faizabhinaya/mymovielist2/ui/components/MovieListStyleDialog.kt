package com.faizabhinaya.mymovielist2.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.faizabhinaya.mymovielist2.data.model.MovieReview

@Composable
fun MovieListStyleDialog(
    movieId: Int,
    movieTitle: String,
    existingReview: MovieReview? = null,
    onDismiss: () -> Unit,
    onSaveReview: (MovieReview) -> Unit,
    onDeleteReview: () -> Unit = {}
) {
    var rating by remember { mutableFloatStateOf(existingReview?.rating ?: 0f) }
    var hoverRating by remember { mutableFloatStateOf(0f) }
    var showReviewInput by remember { mutableStateOf(false) }
    var reviewText by remember { mutableStateOf(existingReview?.review ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Movie Title Header
                Text(
                    text = movieTitle,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                )

                // Rate Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Rate",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // FIXED STAR RATING
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (i in 1..5) {
                            val interactionSource = remember { MutableInteractionSource() }
                            val isHovered by interactionSource.collectIsHoveredAsState()

                            // Pokoknya star should be filled
                            val currentDisplayRating = if (hoverRating > 0) hoverRating else rating
                            val isStarFilled = i <= currentDisplayRating

                            IconButton(
                                onClick = {
                                    rating = i.toFloat()
                                    hoverRating = 0f
                                    // Auto save rating
                                    val review = MovieReview(
                                        id = existingReview?.id ?: "",
                                        movieId = movieId,
                                        movieTitle = movieTitle,
                                        rating = rating,
                                        review = reviewText,
                                        dateCreated = existingReview?.dateCreated ?: System.currentTimeMillis(),
                                        dateModified = System.currentTimeMillis()
                                    )
                                    onSaveReview(review)
                                },
                                modifier = Modifier
                                    .size(44.dp)
                                    .hoverable(interactionSource)
                            ) {
                                Icon(
                                    imageVector = if (isStarFilled) Icons.Filled.Star else Icons.Outlined.Star,
                                    contentDescription = "Star $i",
                                    tint = if (isStarFilled) {
                                        Color(0xFFFFD700) // GOLD/YELLOW COLOR
                                    } else {
                                        Color.Gray.copy(alpha = 0.4f)
                                    },
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            // Handle hover
                            LaunchedEffect(isHovered) {
                                if (isHovered) {
                                    hoverRating = i.toFloat()
                                } else if (hoverRating == i.toFloat()) {
                                    hoverRating = 0f
                                }
                            }
                        }
                    }

                    // Rating display
                    val displayRating = if (hoverRating > 0) hoverRating else rating
                    if (displayRating > 0) {
                        Text(
                            text = String.format("%.1f", displayRating),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                // Divider
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                // Add review section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showReviewInput = !showReviewInput }
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (reviewText.isNotBlank()) "Edit review..." else "Add a review...",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (reviewText.isNotBlank())
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (reviewText.isNotBlank()) FontWeight.Medium else FontWeight.Normal
                    )
                }

                // ENHANCED Review input with DELETE and SAVE buttons
                if (showReviewInput) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .padding(bottom = 20.dp)
                    ) {
                        // Review text field
                        OutlinedTextField(
                            value = reviewText,
                            onValueChange = { reviewText = it },
                            placeholder = { Text("Add a review...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            maxLines = 5,
                            minLines = 4,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                focusedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // DELETE and SAVE buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // DELETE button
                            if (existingReview != null && existingReview.id.isNotEmpty()) {
                                Button(
                                    onClick = {
                                        onDeleteReview()
                                        onDismiss()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        "DELETE",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                // Cancel button for new reviews
                                OutlinedButton(
                                    onClick = {
                                        showReviewInput = false
                                        reviewText = existingReview?.review ?: ""
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("CANCEL")
                                }
                            }

                            // SAVE button
                            Button(
                                onClick = {
                                    val review = MovieReview(
                                        id = existingReview?.id ?: "",
                                        movieId = movieId,
                                        movieTitle = movieTitle,
                                        rating = rating,
                                        review = reviewText,
                                        dateCreated = existingReview?.dateCreated ?: System.currentTimeMillis(),
                                        dateModified = System.currentTimeMillis()
                                    )
                                    onSaveReview(review)
                                    onDismiss()
                                },
                                enabled = rating > 0, // Only enable if rated
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF4CAF50) // Green color
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    "SAVE",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

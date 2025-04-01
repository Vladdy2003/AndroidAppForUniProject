package com.example.metropolitanmuseum.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.metropolitanmuseum.ui.viewmodels.SearchViewModel
import org.koin.androidx.compose.koinViewModel
import com.example.metropolitanmuseum.data.model.ArtObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = koinViewModel(),
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToFavorites: () -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val objectDetails by viewModel.objectDetails.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Metropolitan Museum Explorer") },
                actions = {
                    IconButton(onClick = onNavigateToFavorites) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Favorite"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                label = { Text("Search art") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { viewModel.searchArtObjects() }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = { viewModel.searchArtObjects() }
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                isLoading && searchResults.isEmpty() -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CircularProgressIndicator()
                    }
                }
                error != null -> {
                    Text(
                        text = error ?: "",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                searchResults.isEmpty() && searchQuery.isNotEmpty() -> {
                    Text(
                        text = "No results found for \"$searchQuery\"",
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                searchResults.isNotEmpty() -> {
                    Text(
                        text = "Results: ${searchResults.size}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(searchResults) { objectId ->
                            SearchResultItem(
                                objectId = objectId,
                                viewModel = viewModel,
                                objectDetails = objectDetails[objectId],
                                onItemClick = { onNavigateToDetail(objectId) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResultItem(
    objectId: Int,
    viewModel: SearchViewModel,
    objectDetails: ArtObject?,
    onItemClick: () -> Unit
) {
    // Loading details when the item becomes visible
    LaunchedEffect(objectId) {
        if (objectDetails == null) {
            viewModel.loadObjectDetails(objectId)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onItemClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        if (objectDetails == null) {
            // Loading state
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            }
        } else {
            // Show object details
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Thumbnail image
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .padding(end = 8.dp)
                ) {
                    if (objectDetails.primaryImage.isNotEmpty()) {
                        AsyncImage(
                            model = objectDetails.primaryImage,
                            contentDescription = objectDetails.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                        )
                    } else {
                        // Placeholder if no image
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Image,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                }

                // Title and artist information
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // Object title
                    Text(
                        text = objectDetails.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Artist name
                    Text(
                        text = objectDetails.artistDisplayName.takeIf { it.isNotEmpty() }
                            ?: "Unknown Artist",
                        style = MaterialTheme.typography.bodyMedium,
                        color = LocalContentColor.current.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Arrow icon
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Show details",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}
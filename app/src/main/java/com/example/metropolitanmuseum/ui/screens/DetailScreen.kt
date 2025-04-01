package com.example.metropolitanmuseum.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.metropolitanmuseum.ui.viewmodels.DetailViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: DetailViewModel = koinViewModel(),
    onNavigateBack: () -> Unit
) {
    val artObject by viewModel.artObject.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = artObject?.title ?: "Object Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleFavorite() }) {
                        Icon(
                            if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                            tint = if (isFavorite) Color.Red else LocalContentColor.current.copy(alpha = LocalContentColor.current.alpha)
                        )
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
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                error != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = error ?: "Error",
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                artObject != null -> {
                    artObject?.let { art ->
                        val scrollState = rememberScrollState()

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState)
                                .padding(16.dp)
                        ) {
                            // Imagine principală
                            if (art.primaryImage.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(300.dp)
                                ) {
                                    AsyncImage(
                                        model = art.primaryImage,
                                        contentDescription = art.title,
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .background(Color.LightGray),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No image available")
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Title
                            Text(
                                text = art.title,
                                style = MaterialTheme.typography.headlineMedium
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Info about artist
                            if (art.artistDisplayName.isNotEmpty()) {
                                Text(
                                    text = "Artist: ${art.artistDisplayName}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                            }

                            // Detils
                            DetailItem("Departament", art.department)
                            DetailItem("Object type", art.objectName)
                            DetailItem("Medium", art.medium)
                            DetailItem("Period", art.period)
                            DetailItem("Culture", art.culture)
                            DetailItem("Date", art.objectDate)

                            Spacer(modifier = Modifier.height(16.dp))

                            // Additional images
                            if (art.additionalImages.isNotEmpty()) {
                                Text(
                                    text = "Additional Images",
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )

                                LazyRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentPadding = PaddingValues(8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(art.additionalImages) { imageUrl ->
                                        Card(
                                            modifier = Modifier
                                                .size(160.dp)
                                        ) {
                                            AsyncImage(
                                                model = imageUrl,
                                                contentDescription = null,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    if (value.isNotEmpty()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Text(
                text = "$label:",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(120.dp)
            )
            Text(text = value)
        }
        Divider(modifier = Modifier.padding(vertical = 4.dp))
    }
}
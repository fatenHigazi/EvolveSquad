package com.example.featurevotes.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.featurevotes.R
import com.example.featurevotes.api.FeatureDto

@Composable
fun ListScreen(
    state: MainViewModel.UiState,
    onUpvote: (FeatureDto) -> Unit,
    onRefresh: () -> Unit,
    onNavigateToAdd: () -> Unit,
    scaffoldState: SnackbarHostState
) {
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = scaffoldState) },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAdd) {
                Icon(Icons.Default.Add, contentDescription = "Add new feature")
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                when {
                    state.isLoading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    state.errorMessage != null -> {
                        Text(
                            text = state.errorMessage,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    else -> {
                        FeatureList(
                            features = state.features,
                            onUpvote = onUpvote,
                            upvotingFeatureIds = state.upvotingFeatureIds
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FeatureList(
    features: List<FeatureDto>,
    onUpvote: (FeatureDto) -> Unit,
    upvotingFeatureIds: Set<Int>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.list_title),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        items(features) { feature ->
            FeatureItem(
                feature = feature,
                onUpvote = onUpvote,
                isUpvoting = feature.id in upvotingFeatureIds
            )
        }
    }
}

@Composable
fun FeatureItem(feature: FeatureDto, onUpvote: (FeatureDto) -> Unit, isUpvoting: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = feature.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = feature.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${stringResource(R.string.vote_count_label)} ${feature.voteCount}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Button(
                    onClick = { onUpvote(feature) },
                    enabled = !isUpvoting
                ) {
                    if (isUpvoting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(stringResource(R.string.upvote_button))
                    }
                }
            }
        }
    }
}
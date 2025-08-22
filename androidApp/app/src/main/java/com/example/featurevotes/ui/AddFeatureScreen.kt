package com.example.featurevotes.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.featurevotes.R

@Composable
fun AddFeatureScreen(
    onSubmit: (title: String, description: String) -> Unit,
    onNavigateBack: () -> Unit,
    isLoading: Boolean,
    scaffoldState: SnackbarHostState,
    errorMessage: String? = null
 ) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = scaffoldState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.add_feature_title),
                style = MaterialTheme.typography.headlineMedium
            )
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(R.string.title_hint)) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(R.string.description_hint)) },
                modifier = Modifier.fillMaxWidth().height(200.dp),
                enabled = !isLoading,
                isError = !title.isNullOrBlank() && title.trim().length < 3,
                supportingText = {
                    if (!title.isNullOrBlank() && title.trim().length < 3) {
                          Text("Title must be at least 3 characters.")
            )
            Button(
                onClick = { onSubmit(title.trim(), description.trim()) },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.trim().length >= 3 && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(stringResource(R.string.submit_button))
                }
            }
        }
    }
}
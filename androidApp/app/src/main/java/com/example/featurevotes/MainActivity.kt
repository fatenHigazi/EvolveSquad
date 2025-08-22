package com.example.featurevotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.featurevotes.api.FeatureApiService
import com.example.featurevotes.api.BASE_URL
import com.example.featurevotes.data.FeatureRepository
import com.example.featurevotes.ui.AddFeatureScreen
import com.example.featurevotes.ui.ListScreen
import com.example.featurevotes.ui.MainViewModel
import com.example.featurevotes.ui.MainViewModelFactory
import com.example.featurevotes.ui.theme.FeatureVotesTheme
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import okhttp3.OkHttpClient

sealed class Screen(val route: String) {
    object FeatureList : Screen("featureList")
    object AddFeature : Screen("addFeature")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FeatureVotesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FeatureVotesApp()
                }
            }
        }
    }
}

@Composable
fun FeatureVotesApp() {
    val context = LocalContext.current
    val moshi = remember { Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build() }
    val retrofit = remember {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }
    val apiService = remember { retrofit.create(FeatureApiService::class.java) }
    val repository = remember { FeatureRepository(apiService, context) }
    val viewModel: MainViewModel = viewModel(factory = MainViewModelFactory(repository))
    val navController = rememberNavController()

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    NavHost(navController = navController, startDestination = Screen.FeatureList.route) {
        composable(Screen.FeatureList.route) {
            ListScreen(
                state = viewModel.uiState,
                onUpvote = { feature ->
                    coroutineScope.launch {
                        val result = viewModel.upvoteFeature(feature.id)
                        val message = when (result) {
                            true -> context.getString(R.string.already_upvoted)
                            false -> context.getString(R.string.upvote_success)
                            else -> context.getString(R.string.error_message)
                        }
                        snackbarHostState.showSnackbar(message)
                    }
                },
                onRefresh = viewModel::fetchFeatures,
                onNavigateToAdd = { navController.navigate(Screen.AddFeature.route) },
                scaffoldState = snackbarHostState
            )
        }
        composable(Screen.AddFeature.route) {
            AddFeatureScreen(
                onSubmit = { title, description ->
                    coroutineScope.launch {
                        val success = viewModel.addFeature(title, description)
                        if (success) {
                            navController.popBackStack()
                            viewModel.fetchFeatures() // Refresh list on success
                            snackbarHostState.showSnackbar(context.getString(R.string.add_feature_success))
                        } else {
                            snackbarHostState.showSnackbar(context.getString(R.string.error_message))
                        }
                    }
                },
                onNavigateBack = { navController.popBackStack() },
                isLoading = viewModel.uiState.isLoading,
                scaffoldState = snackbarHostState
            )
        }
    }
}
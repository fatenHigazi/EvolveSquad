package com.example.featurevotes.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.featurevotes.api.FeatureApiService
import com.example.featurevotes.api.FeatureDto
import com.example.featurevotes.api.UpvoteRequestDto
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.util.UUID

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class FeatureRepository(private val apiService: FeatureApiService, private val context: Context) {

    private val VOTER_ID_KEY = stringPreferencesKey("voter_id")

    suspend fun getVoterId(): String {
        val prefs = context.dataStore.data.first()
        var voterId = prefs[VOTER_ID_KEY]
        if (voterId == null) {
            voterId = UUID.randomUUID().toString()
            context.dataStore.edit { settings ->
                settings[VOTER_ID_KEY] = voterId
            }
        }
        return voterId
    }

    suspend fun getFeatures(): Result<List<FeatureDto>> {
        var response: List<FeatureDto>? = null
        var exception: Exception? = null

        // Simple retry logic
        repeat(2) { attempt ->
             try {
                 response = apiService.getFeatures()
                 return Result.success(response!!)
                       } catch (e: Exception) {
                exception = e
                if (it == 0) continue // Retry once
                            else {
                 val errorMessage = "Failed to fetch features. Please check your network."
                   return Result.failure(IOException(errorMessage, exception))
              }
             }
         }
         return Result.failure(IOException("Failed to fetch features after multiple attempts.", exception))
     }
        }
        return Result.failure(IOException("Failed to fetch features after multiple attempts.", exception))
    }

    suspend fun createFeature(title: String, description: String): Result<FeatureDto> {
        return try {
            val response = apiService.createFeature(
                com.example.featurevotes.api.CreateFeatureDto(title, description)
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(IOException("Failed to create feature: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun upvoteFeature(featureId: Int): Result<FeatureDto> {
        return try {
            val voterId = getVoterId()
            val response = apiService.upvoteFeature(featureId, UpvoteRequestDto(voterId))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(IOException("Failed to upvote: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
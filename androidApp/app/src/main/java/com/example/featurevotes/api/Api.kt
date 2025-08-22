package com.example.featurevotes.api

import com.squareup.moshi.JsonClass
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Headers

const val BASE_URL = "http://10.0.2.2:8000/api/"

@JsonClass(generateAdapter = true)
data class FeatureDto(
    val id: Int,
    val title: String,
    val description: String,
    val created_at: String,
    val voteCount: Int
)

@JsonClass(generateAdapter = true)
data class CreateFeatureDto(
    val title: String,
    val description: String
)

@JsonClass(generateAdapter = true)
data class UpvoteRequestDto(
    val voter_id: String
)

@JsonClass(generateAdapter = true)
data class UpvoteResponseDto(
    val already_upvoted: Boolean? = null
)

interface FeatureApiService {
    @GET("features")
    suspend fun getFeatures(): List<FeatureDto>

    @POST("features")
    suspend fun createFeature(@Body feature: CreateFeatureDto): Response<FeatureDto>

    @POST("features/{id}/upvote")
    suspend fun upvoteFeature(@Path("id") id: Int, @Body vote: UpvoteRequestDto): Response<FeatureDto>
}
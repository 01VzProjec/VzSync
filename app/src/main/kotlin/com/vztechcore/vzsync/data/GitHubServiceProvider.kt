package com.vztechcore.vzsync.data

import com.vztechcore.vzsync.data.auth.GitHubApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Single Retrofit instance shared by every repository that talks to GitHub.
 * Avoids duplicating network configuration across repositories.
 */
object GitHubServiceProvider {

    private val json = Json { ignoreUnknownKeys = true }

    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                // TODO: switch to NONE on release builds.
                level = HttpLoggingInterceptor.Level.BASIC
            }
        )
        .build()

    val api: GitHubApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(GitHubApi::class.java)
    }
}
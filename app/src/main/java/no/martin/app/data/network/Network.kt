@file:OptIn(ExperimentalSerializationApi::class)

package no.martin.app.data.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import no.martin.app.data.network.api.AdApi
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

private const val baseUrl = "https://gist.githubusercontent.com/"

fun createAdApi(): AdApi {
    val client = OkHttpClient()
        .newBuilder()
        .build()
    val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }
    val converterFactory = json.asConverterFactory("application/json".toMediaType())
    val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(converterFactory)
        .build()

    return retrofit.create(AdApi::class.java)
}

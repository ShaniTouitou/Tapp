package com.shani.spinwheel.data.remote

import com.shani.spinwheel.data.model.WidgetConfigResponse
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

class ConfigApi(
    private val client: OkHttpClient,
    private val json: Json
) {
    fun fetchConfig(url: String): WidgetConfigResponse {
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IllegalStateException("Failed to fetch config: ${response.code}")
            }

            val body = response.body?.string()
                ?: throw IllegalStateException("Empty config response")

            return json.decodeFromString<WidgetConfigResponse>(body)        }
    }
}
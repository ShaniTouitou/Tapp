package com.shani.spinwheel.data.remote

import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Fetches the raw widget configuration JSON from a remote URL (JSON parsing is in SpinWheelRepository).
 * @param client- An OkHttpClient instance used to execute HTTP requests.
 */
class ConfigApi(private val client: OkHttpClient) {

    // region Public Methods

    fun fetchRawJson(url: String): String {
        val request = Request.Builder().url(url).build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IllegalStateException("Config fetch failed: HTTP ${response.code}")
            return response.body?.string() ?: throw IllegalStateException("Empty config response body")
        }
    }

    // endregion

}

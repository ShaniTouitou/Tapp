package com.shani.spinwheel.data.repository

import android.util.Log
import com.shani.spinwheel.data.cache.ConfigCache
import com.shani.spinwheel.data.cache.WidgetPreferences
import com.shani.spinwheel.data.model.WidgetConfig
import com.shani.spinwheel.data.model.WidgetConfigResponse
import com.shani.spinwheel.data.remote.ConfigApi
import kotlinx.serialization.json.Json

class SpinWheelRepository(
    private val api: ConfigApi,
    private val prefs: WidgetPreferences,
    private val cache: ConfigCache,
    private val json: Json
) {

    // region Public Methods

    suspend fun getConfig(url: String): WidgetConfig {
        return try {
            Log.d("CACHE", "Loading from network")
            val raw = api.fetchRawJson(url)
            cache.save(raw)
            prefs.saveLastFetchTime(System.currentTimeMillis())

            parse(raw)
        } catch (e: Exception) {
            Log.d("CACHE", "Loading from cache")
            val cached = cache.load()
                ?: throw Exception("No cache available")

            parse(cached)
        }
    }

    // endregion

    // region Private Methods

    private fun parse(raw: String): WidgetConfig {
        val wrapper = json.decodeFromString<WidgetConfigResponse>(raw)
        return wrapper.data.first()
    }

    // endregion
}
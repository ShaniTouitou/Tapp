package com.shani.spinwheel.data.repository

import android.util.Log
import com.shani.spinwheel.data.cache.ConfigCache
import com.shani.spinwheel.data.cache.WidgetPreferences
import com.shani.spinwheel.data.model.WidgetConfig
import com.shani.spinwheel.data.model.WidgetConfigResponse
import com.shani.spinwheel.data.remote.ConfigApi
import kotlinx.serialization.json.Json

/**
 * Single source of truth for widget configuration.
 * Always attempts a network fetch first; falls back to the local JSON cache on failure.
 * @param api- The [ConfigApi] instance used for network requests.
 * @param prefs- The [WidgetPreferences] instance used to store metadata like last fetch time.
 * @param cache- The [ConfigCache] instance used to persist the raw JSON locally.
 * @param json- The [Json] instance used for deserializing JSON responses into [WidgetConfig] objects.
 */
class SpinWheelRepository(
    private val api: ConfigApi,
    private val prefs: WidgetPreferences,
    private val cache: ConfigCache,
    private val json: Json
) {

    // region Constant Members

    companion object {
        private const val TAG = "SpinWheelRepository"
    }

    // endregion

    // region Public

    fun getConfig(url: String): WidgetConfig {
        return try {
            val raw = api.fetchRawJson(url)
            cache.save(raw)
            prefs.saveLastFetchTime(System.currentTimeMillis())
            parse(raw)
        } catch (e: Exception) {
            Log.w(TAG, "Network fetch failed, falling back to cache: ${e.message}")
            val cached = cache.load() ?: throw IllegalStateException("No config available: network failed and cache is empty", e)
            parse(cached)
        }
    }

    // endregion

    // region Private

    private fun parse(raw: String): WidgetConfig {
        val wrapper = json.decodeFromString<WidgetConfigResponse>(raw)
        return wrapper.data.firstOrNull()
            ?: throw IllegalStateException("Config response contains an empty data list")
    }

    // endregion

}

package com.shani.spinwheel.data.repository

import com.shani.spinwheel.data.cache.WidgetPreferences
import com.shani.spinwheel.data.model.WidgetConfig
import com.shani.spinwheel.data.remote.ConfigApi

class SpinWheelRepository(
    private val configApi: ConfigApi,
    private val preferences: WidgetPreferences
) {
    fun getConfig(configUrl: String): WidgetConfig {
        val response = configApi.fetchConfig(configUrl)
        preferences.saveLastFetchTime(System.currentTimeMillis())
        return response.data.first()
    }

    fun getLastFetchTime(): Long {
        return preferences.getLastFetchTime()
    }
}
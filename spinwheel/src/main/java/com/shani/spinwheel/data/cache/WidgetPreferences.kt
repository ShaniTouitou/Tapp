package com.shani.spinwheel.data.cache

import android.content.Context
import android.content.SharedPreferences

class WidgetPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("spinwheel_prefs", Context.MODE_PRIVATE)

    fun saveLastFetchTime(timeMillis: Long) {
        prefs.edit().putLong(KEY_LAST_FETCH_TIME, timeMillis).apply()
    }

    fun getLastFetchTime(): Long {
        return prefs.getLong(KEY_LAST_FETCH_TIME, 0L)
    }

    companion object {
        private const val KEY_LAST_FETCH_TIME = "last_fetch_time"
    }
}
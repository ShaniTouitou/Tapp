package com.shani.spinwheel.data.cache

import android.content.Context
import android.content.SharedPreferences

/**
 * This class is a SharedPreferences wrapper used to persist lightweight widget metadata
 * (last successful fetch time).
 * @param context- The Android context used to access the application's cache directory.
 */
class WidgetPreferences(context: Context) {

    // region Private Members

    private val prefs: SharedPreferences =
        context.getSharedPreferences("spinwheel_prefs", Context.MODE_PRIVATE)

    // endregion

    // region Public Methods

    fun saveLastFetchTime(timeMillis: Long) {
        prefs.edit().putLong(KEY_LAST_FETCH_TIME, timeMillis).apply()
    }

    fun getLastFetchTime(): Long {
        return prefs.getLong(KEY_LAST_FETCH_TIME, 0L)
    }

    // endregion

    // region Constants Members

    companion object {
        private const val KEY_LAST_FETCH_TIME = "last_fetch_time"
    }

    // endregion

}
package com.shani.spinwheel.data.cache

import android.content.Context
import java.io.File

/**
 * This class is responsible for
 * caching the remote widget configuration locally for offline fallback.
 * @param context- The Android context used to access the application's cache directory.
 */
class ConfigCache(context: Context) {

    // region Private Members

    private val file = File(context.cacheDir, "widget_config.json")

    // endregion

    // region Public Methods

    fun save(json: String) {
        file.writeText(json)
    }

    fun load(): String? {
        return if (file.exists()) file.readText() else null
    }

    // endregion

}
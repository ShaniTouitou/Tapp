package com.shani.spinwheel.data.cache

import android.content.Context
import java.io.File

/**
 * This class is responsible for storing downloaded asset files locally.
 * @param context- The Android context used to access the application's cache directory.
 */
class AssetFileCache(context: Context) {

    // region Private Members

    private val assetsDir = File(context.cacheDir, "spinwheel_assets").apply { mkdirs() }

    // endregion

    // region Public Methods

    fun getAssetFile(name: String): File {
        return File(assetsDir, name)
    }

    fun exists(name: String): Boolean {
        return getAssetFile(name).exists()
    }

    // endregion

}
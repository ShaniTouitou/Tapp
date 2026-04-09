package com.shani.spinwheel.data.cache

import android.content.Context
import java.io.File

class AssetFileCache(context: Context) {

    // region Private Members

    private val assetsDir = File(context.cacheDir, "spinwheel_assets").apply {
        mkdirs()
    }

    // endregion

    fun getAssetFile(name: String): File {
        return File(assetsDir, name)
    }

    fun exists(name: String): Boolean {
        return getAssetFile(name).exists()
    }
}
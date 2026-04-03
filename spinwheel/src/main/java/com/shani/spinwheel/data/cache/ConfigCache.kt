package com.shani.spinwheel.data.cache

import android.content.Context
import java.io.File

class ConfigCache(context: Context) {

    private val file = File(context.cacheDir, "widget_config.json")

    fun save(json: String) {
        file.writeText(json)
    }

    fun load(): String? {
        return if (file.exists()) file.readText() else null
    }
}
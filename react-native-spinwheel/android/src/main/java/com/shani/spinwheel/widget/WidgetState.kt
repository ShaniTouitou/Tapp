package com.shani.spinwheel.widget

import android.content.Context

/**
 * This class is responsible for manage the widget state using SharedPreferences.
 */
object WidgetState {

    // region Constant Members

    private const val PREFS_NAME = "spinwheel_widget_state"
    private const val KEY_CONFIG_URL = "config_url"
    private const val KEY_STATUS = "status"
    private const val KEY_RESULT = "result"
    private const val KEY_ROTATION = "rotation"
    private const val KEY_MIN_SPINS = "min_spins"
    private const val KEY_MAX_SPINS = "max_spins"
    private const val KEY_DURATION_MS = "duration_ms"
    private const val KEY_BG_PATH = "bg_path"
    private const val KEY_WHEEL_PATH = "wheel_path"
    private const val KEY_FRAME_PATH = "frame_path"
    private const val KEY_SPIN_PATH = "spin_path"

    // endregion

    // region Public Getters / Setters Methods

    fun getConfigUrl(context: Context): String? =
        prefs(context).getString(KEY_CONFIG_URL, null)

    fun setConfigUrl(context: Context, url: String) {
        prefs(context).edit().putString(KEY_CONFIG_URL, url).apply()
    }

    fun getStatus(context: Context): String =
        prefs(context).getString(KEY_STATUS, "Loading") ?: "Loading"

    fun setStatus(context: Context, value: String) {
        prefs(context).edit().putString(KEY_STATUS, value).apply()
    }

    fun getResult(context: Context): String =
        prefs(context).getString(KEY_RESULT, "-") ?: "-"

    fun setResult(context: Context, value: String) {
        prefs(context).edit().putString(KEY_RESULT, value).apply()
    }

    fun getRotation(context: Context): Float =
        prefs(context).getFloat(KEY_ROTATION, 0f)

    fun setRotation(context: Context, value: Float) {
        prefs(context).edit().putFloat(KEY_ROTATION, value).apply()
    }

    fun getMinSpins(context: Context): Int =
        prefs(context).getInt(KEY_MIN_SPINS, 3)

    fun getMaxSpins(context: Context): Int =
        prefs(context).getInt(KEY_MAX_SPINS, 5)

    fun getDurationMs(context: Context): Int =
        prefs(context).getInt(KEY_DURATION_MS, 2000)

    fun setRotationConfig(context: Context, minSpins: Int, maxSpins: Int, durationMs: Int) {
        prefs(context).edit()
            .putInt(KEY_MIN_SPINS, minSpins)
            .putInt(KEY_MAX_SPINS, maxSpins)
            .putInt(KEY_DURATION_MS, durationMs)
            .apply()
    }

    fun setAssetPaths(
        context: Context,
        bgPath: String?,
        wheelPath: String?,
        framePath: String?,
        spinPath: String?
    ) {
        prefs(context).edit()
            .putString(KEY_BG_PATH, bgPath)
            .putString(KEY_WHEEL_PATH, wheelPath)
            .putString(KEY_FRAME_PATH, framePath)
            .putString(KEY_SPIN_PATH, spinPath)
            .apply()
    }

    fun getBgPath(context: Context): String? =
        prefs(context).getString(KEY_BG_PATH, null)

    fun getWheelPath(context: Context): String? =
        prefs(context).getString(KEY_WHEEL_PATH, null)

    fun getFramePath(context: Context): String? =
        prefs(context).getString(KEY_FRAME_PATH, null)

    fun getSpinPath(context: Context): String? =
        prefs(context).getString(KEY_SPIN_PATH, null)

    // endregion

    // region Private Helpers

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // endregion

}
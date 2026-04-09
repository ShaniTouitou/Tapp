package com.shani.spinwheel.widget

import android.content.Context
import android.util.Log
import com.shani.spinwheel.data.cache.AssetFileCache
import com.shani.spinwheel.data.cache.ConfigCache
import com.shani.spinwheel.data.cache.WidgetPreferences
import com.shani.spinwheel.data.remote.AssetDownloader
import com.shani.spinwheel.data.remote.ConfigApi
import com.shani.spinwheel.data.repository.AssetRepository
import com.shani.spinwheel.data.repository.SpinWheelRepository
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient

/**
 * This class is responsible for fetching remote configuration,
 * downloading required assets,and updating the widget state.
 */
object SpinWheelWidgetUpdater {

    // region Constants Members

    private const val TAG = "SpinWheelUpdater"

    private const val DEFAULT_CONFIG_URL =
        "https://gist.githubusercontent.com/ShaniTouitou/fa5024f0e34c804a7e41635e100e8f85/raw/gistfile1.txt"

    private const val STATUS_LOADING = "Loading"
    private const val STATUS_READY = "Ready"
    private const val STATUS_OFFLINE = "Offline cache"

    private const val DEFAULT_RESULT = "-"
    private const val DEFAULT_RESULT_TEXT = "Tap SPIN"

    private const val FILE_NAME_BG = "widget_bg"
    private const val FILE_NAME_WHEEL = "widget_wheel"
    private const val FILE_NAME_FRAME = "widget_frame"
    private const val FILE_NAME_SPIN = "widget_spin"

    // endregion

    // region Singleton Dependencies

    private val httpClient = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    // endregion

    // region Public Methods

    fun saveConfigUrl(context: Context, url: String) {
        WidgetState.setConfigUrl(context, url)
    }

    suspend fun refreshFromRemote(context: Context) {
        val url = WidgetState.getConfigUrl(context) ?: DEFAULT_CONFIG_URL

        val configApi = ConfigApi(httpClient)
        val spinRepo = SpinWheelRepository(
            api = configApi,
            prefs = WidgetPreferences(context),
            cache = ConfigCache(context),
            json = json
        )
        val assetRepo = AssetRepository(
            downloader = AssetDownloader(httpClient),
            cache = AssetFileCache(context)
        )

        try {
            WidgetState.setStatus(context, STATUS_LOADING)

            val config = spinRepo.getConfig(url)
            val host = config.network.assets.host
            val assets = config.wheel.assets
            val rotation = config.wheel.rotation

            val bg = assetRepo.getOrDownloadAsset(host + assets.bg, FILE_NAME_BG)
            val wheel = assetRepo.getOrDownloadAsset(host + assets.wheel, FILE_NAME_WHEEL)
            val frame = assetRepo.getOrDownloadAsset(host + assets.wheelFrame, FILE_NAME_FRAME)
            val spin = assetRepo.getOrDownloadAsset(host + assets.wheelSpin, FILE_NAME_SPIN)

            WidgetState.setAssetPaths(
                context = context,
                bgPath = bg?.absolutePath,
                wheelPath = wheel?.absolutePath,
                framePath = frame?.absolutePath,
                spinPath = spin?.absolutePath
            )

            WidgetState.setRotationConfig(
                context = context,
                minSpins = rotation.minimumSpins,
                maxSpins = rotation.maximumSpins,
                durationMs = rotation.duration
            )

            WidgetState.setStatus(context, STATUS_READY)

            if (WidgetState.getResult(context) == DEFAULT_RESULT) {
                WidgetState.setResult(context, DEFAULT_RESULT_TEXT)
            }

        } catch (e: Exception) {
            Log.e(TAG, "Remote refresh failed, falling back to cache: ${e.message}", e)
            WidgetState.setStatus(context, STATUS_OFFLINE)
        } finally {
            SpinWheelWidgetReceiver.updateAllWidgets(context)
        }
    }

    // endregion

}
package com.shani.spinwheel

import android.content.Context
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shani.spinwheel.presentation.ui.BrandColor
import com.shani.spinwheel.presentation.ui.BrandTextStyle
import com.shani.spinwheel.presentation.ui.PoppinsBold
import com.shani.spinwheel.data.cache.AssetFileCache
import com.shani.spinwheel.data.cache.ConfigCache
import com.shani.spinwheel.data.cache.WidgetPreferences
import com.shani.spinwheel.data.model.WidgetConfig
import com.shani.spinwheel.data.remote.AssetDownloader
import com.shani.spinwheel.data.remote.ConfigApi
import com.shani.spinwheel.data.repository.AssetRepository
import com.shani.spinwheel.data.repository.SpinWheelRepository
import com.shani.spinwheel.presentation.ui.SpinWheelRemoteScreen
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import java.util.concurrent.Executors

/**
 * This class is the native Android view
 * that manages loading state, configuration handling, and rendering of the Spin Wheel UI.
 */
class SpinWheelView(context: Context) : FrameLayout(context) {

    // region Private Members

    private val composeView = ComposeView(context)
    private val executor = Executors.newSingleThreadExecutor()

    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    private val configApi = ConfigApi(client)
    private val configCache = ConfigCache(context)
    private val widgetPrefs = WidgetPreferences(context)
    private val spinWheelRepository = SpinWheelRepository(configApi, widgetPrefs, configCache, json)

    private val assetDownloader = AssetDownloader(client)
    private val assetFileCache = AssetFileCache(context)
    private val assetRepository = AssetRepository(
        downloader = assetDownloader,
        cache = assetFileCache
    )

    private var configUrl: String? = null
    private var widgetConfig: WidgetConfig? = null
    private var isLoading = false
    private var isInitialized = false
    private var errorMessage: String? = null

    // endregion

    // region Init Method

    init {
        addView(
            composeView,
            LayoutParams(MATCH_PARENT, MATCH_PARENT)
        )
        render()
    }

    // endregion

    // region Public Methods

    fun setConfigUrl(url: String) {
        configUrl = url
    }

    fun loadConfig() {
        val url = configUrl ?: return

        isInitialized = true
        isLoading = true
        errorMessage = null
        render()

        executor.execute {
            try {
                val config = spinWheelRepository.getConfig(url)

                post {
                    widgetConfig = config
                    isLoading = false
                    errorMessage = null
                    render()
                }
            } catch (e: Exception) {
                post {
                    widgetConfig = null
                    isLoading = false
                    errorMessage = e.message ?: "Failed to load config"
                    render()
                }
            }
        }
    }

    // endregion

    // region Private Methods

    private fun render() {
        composeView.setContent {
            val item = widgetConfig

            when {
                !isInitialized || isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            CircularProgressIndicator(color = BrandColor)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Loading...", style = BrandTextStyle)
                        }
                    }
                }

                errorMessage != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(errorMessage ?: "Unknown error", style = BrandTextStyle)
                    }
                }

                item == null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No widget config available", style = BrandTextStyle)
                    }
                }

                else -> {
                    val host = item.network.assets.host
                    val assets = item.wheel.assets
                    val rotation = item.wheel.rotation

                    SpinWheelRemoteScreen(
                        backgroundUrl = host + assets.bg,
                        wheelUrl = host + assets.wheel,
                        frameUrl = host + assets.wheelFrame,
                        spinButtonUrl = host + assets.wheelSpin,
                        minimumSpins = rotation.minimumSpins,
                        maximumSpins = rotation.maximumSpins,
                        durationMillis = rotation.duration,
                        assetRepository = assetRepository
                    )
                }
            }
        }
    }

    // endregion

    // region Override Methods

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        executor.shutdown()
    }

    // endregion

}
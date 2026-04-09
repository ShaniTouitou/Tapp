package com.shani.spinwheel

import android.content.Context
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.compose.material3.Text
import androidx.compose.ui.platform.ComposeView
import com.shani.spinwheel.data.cache.AssetFileCache
import com.shani.spinwheel.data.model.WidgetConfigResponse
import com.shani.spinwheel.data.remote.AssetDownloader
import com.shani.spinwheel.data.remote.ConfigApi
import com.shani.spinwheel.data.repository.AssetRepository
import com.shani.spinwheel.presentation.ui.SpinWheelRemoteScreen
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import java.util.concurrent.Executors

class SpinWheelView(context: Context) : FrameLayout(context) {

    // region Private Members

    private val composeView = ComposeView(context)
    private val executor = Executors.newSingleThreadExecutor()

    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    private val configApi = ConfigApi(client, json)

    private val assetDownloader = AssetDownloader(client)
    private val assetFileCache = AssetFileCache(context)
    private val assetRepository = AssetRepository(
        downloader = assetDownloader,
        cache = assetFileCache
    )

    private var configUrl: String? = null
    private var widgetConfig: WidgetConfigResponse? = null
    private var isLoading = false
    private var errorMessage: String? = null

    // endregion

    init {
        addView(
            composeView,
            LayoutParams(MATCH_PARENT, MATCH_PARENT)
        )
        render()
    }

    // region Public Methods

    fun setConfigUrl(url: String) {
        configUrl = url
    }

    fun loadConfig() {
        val url = configUrl ?: return

        isLoading = true
        errorMessage = null
        render()

        executor.execute {
            try {
                val config = configApi.fetchConfig(url)

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
            val config = widgetConfig
            val item = config?.data?.firstOrNull()

            when {
                isLoading -> {
                    Text("Loading config...")
                }

                errorMessage != null -> {
                    Text(errorMessage ?: "Unknown error")
                }

                item == null -> {
                    Text("No widget config available")
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
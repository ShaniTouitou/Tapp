package com.example.tappspinwheel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shani.spinwheel.data.cache.AssetFileCache
import com.shani.spinwheel.data.cache.ConfigCache
import com.shani.spinwheel.data.cache.WidgetPreferences
import com.shani.spinwheel.data.model.WidgetConfig
import com.shani.spinwheel.data.remote.AssetDownloader
import com.shani.spinwheel.data.remote.ConfigApi
import com.shani.spinwheel.data.repository.AssetRepository
import com.shani.spinwheel.data.repository.SpinWheelRepository
import com.shani.spinwheel.presentation.ui.SpinWheelRemoteScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient

class MainActivity : ComponentActivity() {

    companion object {
        const val CONFIG_URL = "https://gist.githubusercontent.com/ShaniTouitou/fa5024f0e34c804a7e41635e100e8f85/raw/2029cb0b91b7db51624e39e87821b584dcd3d9cb/gistfile1.txt"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val client = OkHttpClient()
        val json = Json { ignoreUnknownKeys = true }

        val configApi = ConfigApi(client, json)
        val prefs = WidgetPreferences(this)
        val configCache = ConfigCache(this)

        val repository = SpinWheelRepository(
            api = configApi,
            prefs = prefs,
            cache = configCache,
            json = json
        )

        val assetDownloader = AssetDownloader(client)
        val assetCache = AssetFileCache(this)
        val assetRepository = AssetRepository(assetDownloader, assetCache)

        setContent {
            MaterialTheme {
                WidgetHost(
                    repository = repository,
                    assetRepository = assetRepository
                )
            }
        }
    }
}

@Composable
fun WidgetHost(
    repository: SpinWheelRepository,
    assetRepository: AssetRepository
) {
    var config by remember { mutableStateOf<WidgetConfig?>(null) }
    var errorText by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            config = withContext(Dispatchers.IO) {
                repository.getConfig(MainActivity.CONFIG_URL)
            }
        } catch (e: Exception) {
            errorText = e.message ?: "Unknown error"
        }
    }

    when {
        errorText != null -> {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Error: $errorText",
                    modifier = Modifier.padding(24.dp)
                )
            }
        }

        config == null -> {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Loading config...",
                    modifier = Modifier.padding(24.dp)
                )
            }
        }

        else -> {
            val widgetConfig = config!!
            val host = widgetConfig.network.assets.host

            val backgroundUrl = host + widgetConfig.wheel.assets.bg
            val wheelUrl = host + widgetConfig.wheel.assets.wheel
            val frameUrl = host + widgetConfig.wheel.assets.wheelFrame
            val spinButtonUrl = host + widgetConfig.wheel.assets.wheelSpin

            val rotation = widgetConfig.wheel.rotation

            SpinWheelRemoteScreen(
                backgroundUrl = backgroundUrl,
                wheelUrl = wheelUrl,
                frameUrl = frameUrl,
                spinButtonUrl = spinButtonUrl,
                minimumSpins = rotation.minimumSpins,
                maximumSpins = rotation.maximumSpins,
                durationMillis = rotation.duration,
                assetRepository = assetRepository
            )
        }
    }
}
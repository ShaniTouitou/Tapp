package com.example.tappspinwheel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shani.spinwheel.data.cache.WidgetPreferences
import com.shani.spinwheel.data.model.WidgetConfig
import com.shani.spinwheel.data.remote.ConfigApi
import com.shani.spinwheel.data.repository.SpinWheelRepository
import com.example.tappspinwheel.MainActivity.Companion.CONFIG_URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import com.shani.spinwheel.presentation.ui.SpinWheelLocalScreen

class MainActivity : ComponentActivity() {

    companion object {
        const val CONFIG_URL = "https://gist.githubusercontent.com/ShaniTouitou/fa5024f0e34c804a7e41635e100e8f85/raw/f6e05ad2b3487be5655d7d50e86dad23ced4d879/gistfile1.txt"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val client = OkHttpClient()
        val json = Json {
            ignoreUnknownKeys = true
        }

        val api = ConfigApi(client, json)
        val prefs = WidgetPreferences(this)
        val repository = SpinWheelRepository(api, prefs)

        setContent {
            MaterialTheme {
                SpinWheelLocalScreen()
            }
        }
    }
}

@Composable
fun ConfigTestScreen(
    repository: SpinWheelRepository
) {
    var configName by remember { mutableStateOf("Not loaded yet") }
    var lastFetchTime by remember { mutableStateOf("No fetch yet") }
    var errorText by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(
            onClick = {
                scope.launch {
                    errorText = null
                    try {
                        val config: WidgetConfig = withContext(Dispatchers.IO) {
                            repository.getConfig(CONFIG_URL)
                        }

                        configName = config.name

                        val fetchTime = repository.getLastFetchTime()
                        lastFetchTime = fetchTime.toString()
                    } catch (e: Exception) {
                        errorText = e.message ?: "Unknown error"
                    }
                }
            }
        ) {
            Text("Load Config")
        }

        Text("Config name: $configName")
        Text("Last fetch time: $lastFetchTime")

        errorText?.let {
            Text("Error: $it")
        }
    }
}
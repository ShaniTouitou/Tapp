package com.shani.spinwheel.presentation.ui

import android.graphics.BitmapFactory
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.sp
import com.shani.spinwheel.data.repository.AssetRepository
import com.shani.spinwheel.presentation.ui.BrandTextStyle
import kotlinx.coroutines.launch
import java.io.File
import kotlin.random.Random

/**
 * This file contains the Compose UI responsible for
 * rendering the Spin Wheel using locally cached asset files.
 */

// region Private Methods

private fun urlToFileName(url: String, prefix: String): String {
    val hash = url.hashCode().toLong() and 0xFFFFFFFFL
    return "${prefix}_${hash}.png"
}

@Composable
private fun rememberBitmap(file: File): ImageBitmap? {
    return remember(file) {
        BitmapFactory.decodeFile(file.absolutePath)?.asImageBitmap()
    }
}

// endregion

// region Public Methods

@Composable
fun SpinWheelRemoteScreen(
    backgroundUrl: String,
    wheelUrl: String,
    frameUrl: String,
    spinButtonUrl: String,
    minimumSpins: Int,
    maximumSpins: Int,
    durationMillis: Int,
    assetRepository: AssetRepository
) {
    val rotation = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    var isSpinning by remember { mutableStateOf(false) }

    val bgState by rememberCachedImageFile(backgroundUrl, urlToFileName(backgroundUrl, "bg"), assetRepository)
    val wheelState by rememberCachedImageFile(wheelUrl, urlToFileName(wheelUrl, "wheel"), assetRepository)
    val frameState by rememberCachedImageFile(frameUrl, urlToFileName(frameUrl, "wheel_frame"), assetRepository)
    val spinState by rememberCachedImageFile(spinButtonUrl, urlToFileName(spinButtonUrl, "wheel_spin"), assetRepository)

    fun randomTargetRotation(): Float {
        val spins = Random.nextInt(minimumSpins, maximumSpins + 1)
        val extraDegrees = Random.nextInt(0, 360)
        return rotation.value + spins * 360f + extraDegrees
    }

    val allLoaded = bgState is AssetLoadState.Success &&
            wheelState is AssetLoadState.Success &&
            frameState is AssetLoadState.Success &&
            spinState is AssetLoadState.Success

    val anyError = listOf(bgState, wheelState, frameState, spinState)
        .filterIsInstance<AssetLoadState.Error>()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (allLoaded) {
            val bgFile = (bgState as AssetLoadState.Success).file
            val wheelFile = (wheelState as AssetLoadState.Success).file
            val frameFile = (frameState as AssetLoadState.Success).file
            val spinFile = (spinState as AssetLoadState.Success).file

            val bgBitmap = rememberBitmap(bgFile)
            val wheelBitmap = rememberBitmap(wheelFile)
            val frameBitmap = rememberBitmap(frameFile)
            val spinBitmap = rememberBitmap(spinFile)

            bgBitmap?.let { bitmap ->
                Image(
                    bitmap = bitmap,
                    contentDescription = "Background",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            wheelBitmap?.let { bitmap ->
                Image(
                    bitmap = bitmap,
                    contentDescription = "Wheel",
                    modifier = Modifier
                        .size(250.dp)
                        .rotate(rotation.value)
                )
            }

            frameBitmap?.let { bitmap ->
                Image(
                    bitmap = bitmap,
                    contentDescription = "Frame",
                    modifier = Modifier.size(300.dp)
                )
            }

            spinBitmap?.let { bitmap ->
                Image(
                    bitmap = bitmap,
                    contentDescription = "Spin Button",
                    modifier = Modifier
                        .size(100.dp)
                        .graphicsLayer {
                            alpha = 0.99f
                            compositingStrategy = CompositingStrategy.Offscreen
                        }
                        .pointerInput(isSpinning) {
                            detectTapGestures {
                                if (!isSpinning) {
                                    scope.launch {
                                        isSpinning = true
                                        rotation.animateTo(
                                            targetValue = randomTargetRotation(),
                                            animationSpec = tween(durationMillis)
                                        )
                                        isSpinning = false
                                    }
                                }
                            }
                        }
                )
            }
        } else if (anyError.isNotEmpty()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text(text = anyError.joinToString("\n") { it.message }, style = BrandTextStyle)
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                CircularProgressIndicator(color = BrandTextStyle.color)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Loading assets...", style = BrandTextStyle)
            }
        }
    }
}

// endregion
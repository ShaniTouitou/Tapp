package com.shani.spinwheel.presentation.ui

import android.graphics.BitmapFactory
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.shani.spinwheel.data.repository.AssetRepository
import kotlinx.coroutines.launch
import kotlin.random.Random

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

    // region Members

    val rotation = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    val bgState by rememberCachedImageFile(backgroundUrl, "bg.png", assetRepository)
    val wheelState by rememberCachedImageFile(wheelUrl, "wheel.png", assetRepository)
    val frameState by rememberCachedImageFile(frameUrl, "wheel_frame.png", assetRepository)
    val spinState by rememberCachedImageFile(spinButtonUrl, "wheel_spin.png", assetRepository)

    // endregion

    // region Public Methods

    fun randomTargetRotation(): Float {
        val spins = Random.nextInt(minimumSpins, maximumSpins + 1)
        return rotation.value + spins * 360f
    }

    // endregion

    val allLoaded = bgState is AssetLoadState.Success &&
            wheelState is AssetLoadState.Success &&
            frameState is AssetLoadState.Success &&
            spinState is AssetLoadState.Success

    val anyError = listOf(bgState, wheelState, frameState, spinState).filterIsInstance<AssetLoadState.Error>()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (allLoaded) {
            val bgFile = (bgState as AssetLoadState.Success).file
            val wheelFile = (wheelState as AssetLoadState.Success).file
            val frameFile = (frameState as AssetLoadState.Success).file
            val spinFile = (spinState as AssetLoadState.Success).file

            BitmapFactory.decodeFile(bgFile.absolutePath)?.let { bitmap ->
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Background",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            BitmapFactory.decodeFile(wheelFile.absolutePath)?.let { bitmap ->
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Wheel",
                    modifier = Modifier
                        .size(250.dp)
                        .rotate(rotation.value)
                )
            }

            BitmapFactory.decodeFile(frameFile.absolutePath)?.let { bitmap ->
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Frame",
                    modifier = Modifier.size(300.dp)
                )
            }

            BitmapFactory.decodeFile(spinFile.absolutePath)?.let { bitmap ->
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Spin Button",
                    modifier = Modifier
                        .size(100.dp)
                        .clickable {
                            scope.launch {
                                rotation.animateTo(
                                    targetValue = randomTargetRotation(),
                                    animationSpec = tween(durationMillis)
                                )
                            }
                        }
                )
            }
        } else if (anyError.isNotEmpty()) {
            Text(
                text = anyError.joinToString("\n") { it.message }
            )
        } else {
            Text("Loading assets...")
        }
    }
}
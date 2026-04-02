package com.shani.spinwheel.presentation.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.shani.spinwheel.R
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun SpinWheelLocalScreen(
    minimumSpins: Int = 3,
    maximumSpins: Int = 5,
    durationMillis: Int = 2000
) {
    val rotation = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    fun randomTargetRotation(): Float {
        val spins = Random.nextInt(minimumSpins, maximumSpins + 1)
        return rotation.value + spins * 360f
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Image(
            painter = painterResource(id = R.drawable.wheel),
            contentDescription = "Wheel",
            modifier = Modifier
                .size(260.dp)
                .rotate(rotation.value),
            contentScale = ContentScale.Fit
        )

        Image(
            painter = painterResource(id = R.drawable.wheel_frame),
            contentDescription = "Wheel frame",
            modifier = Modifier.size(285.dp),
            contentScale = ContentScale.Fit
        )

        Image(
            painter = painterResource(id = R.drawable.wheel_spin),
            contentDescription = "Spin button",
            modifier = Modifier
                .size(84.dp)
                .clickable {
                    scope.launch {
                        rotation.animateTo(
                            targetValue = randomTargetRotation(),
                            animationSpec = tween(durationMillis)
                        )
                    }
                },
            contentScale = ContentScale.Fit
        )
    }
}
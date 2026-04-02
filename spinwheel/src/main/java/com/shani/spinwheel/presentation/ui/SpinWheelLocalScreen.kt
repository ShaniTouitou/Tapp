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
import coil.compose.AsyncImage

@Composable
fun SpinWheelLocalScreen(
    minimumSpins: Int = 3,
    maximumSpins: Int = 5,
    durationMillis: Int = 2000
) {
    val rotation = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    val bg = "https://drive.google.com/uc?export=view&id=10V3f826CIbrj-ltfQPKFNXDnY04gOzq-"
    val wheelFrame = "https://drive.google.com/uc?export=view&id=1wvd9j4o9eqHaB1WewMLHsPqbQS8Ziyhz"
    val wheelSpin = "https://drive.google.com/uc?export=view&id=1sL_zQT3QGqlwhFG8ov4-SzwXZp3OyJ7H"
    val wheel = "https://drive.google.com/uc?export=view&id=1pZ0BPCEJQq_2hjGb4_m9M05cqqE3D3aX"

    fun randomTargetRotation(): Float {
        val spins = Random.nextInt(minimumSpins, maximumSpins + 1)
        return rotation.value + spins * 360f
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = bg,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        AsyncImage(
            model = wheel,
            contentDescription = null,
            modifier = Modifier
                .size(250.dp)
                .rotate(rotation.value)
        )

        AsyncImage(
            model = wheelFrame,
            contentDescription = null,
            modifier = Modifier.size(300.dp)
        )
        
        AsyncImage(
            model = wheelSpin,
            contentDescription = null,
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
}
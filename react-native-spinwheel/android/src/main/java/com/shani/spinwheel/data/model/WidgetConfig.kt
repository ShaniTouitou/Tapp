package com.shani.spinwheel.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * This class is the data model that represents the structure of the remote widget configuration JSON.
 */
@Serializable
data class WidgetConfigResponse(
    val data: List<WidgetConfig>,
    val meta: Meta
)

@Serializable
data class WidgetConfig(
    val id: String,
    val name: String,
    val type: String,
    val network: NetworkConfig,
    val wheel: WheelConfig
)

@Serializable
data class Meta(
    val version: Int,
    val copyright: String
)

@Serializable
data class NetworkConfig(
    val attributes: NetworkAttributes,
    val assets: NetworkAssets
)

@Serializable
data class NetworkAttributes(
    val refreshInterval: Int,
    val networkTimeout: Int,
    val retryAttempts: Int,
    val cacheExpiration: Int,
    val debugMode: Boolean
)

@Serializable
data class NetworkAssets(
    val host: String
)

@Serializable
data class WheelConfig(
    val rotation: RotationConfig,
    val assets: WheelAssets
)

@Serializable
data class RotationConfig(
    val duration: Int,
    val minimumSpins: Int,
    val maximumSpins: Int,
    val spinEasing: String
)

@Serializable
data class WheelAssets(
    val bg: String,
    @SerialName("wheelFrame")
    val wheelFrame: String,
    @SerialName("wheelSpin")
    val wheelSpin: String,
    val wheel: String
)
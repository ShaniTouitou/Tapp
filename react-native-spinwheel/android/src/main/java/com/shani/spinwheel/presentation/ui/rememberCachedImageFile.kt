package com.shani.spinwheel.presentation.ui

import androidx.compose.runtime.*
import com.shani.spinwheel.data.repository.AssetRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * This file contains helper logic for remembering cached image files across recompositions
 * (avoid unnecessary reloads in Compose).
 */

sealed class AssetLoadState {
    data object Loading : AssetLoadState()
    data class Success(val file: File) : AssetLoadState()
    data class Error(val message: String) : AssetLoadState()
}

// region Public Methods

@Composable
fun rememberCachedImageFile(
    url: String,
    fileName: String,
    repository: AssetRepository
): State<AssetLoadState> {
    val state = remember(url) { mutableStateOf<AssetLoadState>(AssetLoadState.Loading) }

    LaunchedEffect(url) {
        state.value = withContext(Dispatchers.IO) {
            val file = repository.getOrDownloadAsset(url, fileName)
            if (file != null && file.exists() && file.length() > 0) {
                AssetLoadState.Success(file)
            } else {
                AssetLoadState.Error("Failed to load $fileName")
            }
        }
    }

    return state
}

// endregion
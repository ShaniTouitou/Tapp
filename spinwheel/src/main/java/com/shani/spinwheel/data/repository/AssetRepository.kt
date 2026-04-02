package com.shani.spinwheel.data.repository

import com.shani.spinwheel.data.cache.AssetFileCache
import com.shani.spinwheel.data.remote.AssetDownloader
import java.io.File

class AssetRepository(
    private val downloader: AssetDownloader,
    private val cache: AssetFileCache
) {
    fun getOrDownloadAsset(url: String, fileName: String): File? {
        val cachedFile = cache.getAssetFile(fileName)

        if (cachedFile.exists()) {
            return cachedFile
        }

        return downloader.downloadToFile(url, cachedFile)
    }
}
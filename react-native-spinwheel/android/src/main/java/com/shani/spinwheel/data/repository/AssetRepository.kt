package com.shani.spinwheel.data.repository

import com.shani.spinwheel.data.cache.AssetFileCache
import com.shani.spinwheel.data.remote.AssetDownloader
import java.io.File

/**
 * This class is the repository layer that coordinates remote asset downloading and local asset caching.
 * @param downloader- An instance of [AssetDownloader] used to fetch remote assets.
 * @param cache- An instance of [AssetFileCache] used to store and retrieve cached assets.
 */
class AssetRepository(
    private val downloader: AssetDownloader,
    private val cache: AssetFileCache
) {

    // region Public Methods

    fun getOrDownloadAsset(url: String, fileName: String): File? {
        val cachedFile = cache.getAssetFile(fileName)

        if (cachedFile.exists()) {
            return cachedFile
        }

        return downloader.downloadToFile(url, cachedFile)
    }

    // endregion

}
package com.shani.spinwheel.data.remote

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

/**
 * This class is responsible for downloads image assets from remote URLs,
 * including Google Drive direct-download links.
 * @param client- An OkHttpClient instance used to execute HTTP requests.
 */
class AssetDownloader(private val client: OkHttpClient) {

    // region Constants Members

    companion object {
        private const val TAG = "AssetDownloader"
        private const val DRIVE_BASE_DOWNLOAD_URL = "https://drive.google.com/uc?export=download&id="
        private val DRIVE_FILE_ID_REGEX = Regex("(?:/d/|id=)([a-zA-Z0-9_-]{10,})")
        private val DRIVE_CONFIRM_REGEX = Regex("confirm=([a-zA-Z0-9_-]+)")
    }

    // endregion

    // region Public Methods

    fun downloadToFile(url: String, outputFile: File): File? {
        val resolvedUrl = resolveGoogleDriveUrl(url)
        val request = Request.Builder().url(resolvedUrl).build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                Log.w(TAG, "Failed to download $resolvedUrl — HTTP ${response.code}")
                return null
            }

            val body = response.body ?: return null
            val contentType = response.header("Content-Type") ?: ""

            if (contentType.contains("text/html")) {
                Log.w(TAG, "Got HTML instead of image for ${outputFile.name} — retrying with confirm token")
                return downloadGoogleDriveConfirm(resolvedUrl, outputFile)
            }

            outputFile.outputStream().use { body.byteStream().copyTo(it) }
            return outputFile
        }
    }

    // endregion

    // region Private Methods

    private fun resolveGoogleDriveUrl(url: String): String {
        val match = DRIVE_FILE_ID_REGEX.find(url)
        return if (match != null) {
            "$DRIVE_BASE_DOWNLOAD_URL${match.groupValues[1]}"
        } else url
    }

    private fun downloadGoogleDriveConfirm(url: String, outputFile: File): File? {
        val confirmToken = client.newCall(Request.Builder().url(url).build()).execute().use { response ->
            response.body?.string()?.let { DRIVE_CONFIRM_REGEX.find(it)?.groupValues?.get(1) }
        }

        if (confirmToken == null) {
            Log.w(TAG, "Could not extract Drive confirm token for ${outputFile.name}")
            return null
        }

        val confirmUrl = "$url&confirm=$confirmToken"
        client.newCall(Request.Builder().url(confirmUrl).build()).execute().use { response ->
            if (!response.isSuccessful) return null
            val body = response.body ?: return null
            outputFile.outputStream().use { body.byteStream().copyTo(it) }
            return outputFile
        }
    }

    // endregion

}

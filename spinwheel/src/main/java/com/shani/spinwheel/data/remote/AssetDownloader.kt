package com.shani.spinwheel.data.remote

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

class AssetDownloader(
    private val client: OkHttpClient
) {
    fun downloadToFile(url: String, outputFile: File): File? {
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        client.newCall(request).execute().use { response ->
            Log.d("AssetDownloader", "URL: $url")
            Log.d("AssetDownloader", "Code: ${response.code}")
            Log.d("AssetDownloader", "Content-Type: ${response.header("Content-Type")}")

            if (!response.isSuccessful) return null

            val body = response.body ?: return null

            outputFile.outputStream().use { output ->
                body.byteStream().copyTo(output)
            }

            Log.d("AssetDownloader", "Saved ${outputFile.name}, size=${outputFile.length()}")
            return outputFile
        }
    }
}
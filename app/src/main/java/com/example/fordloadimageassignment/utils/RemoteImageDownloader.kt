package com.example.fordloadimageassignment.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import com.example.fordloadimageassignment.models.ImageItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

class RemoteImageDownloader(private val context: Context) {

    suspend fun downloadImage(width: Int, height: Int, text: String? = null): ImageItem = withContext(Dispatchers.IO) {
        val imageUrl = if (text != null)
            "https://placehold.co/${width}x${height}.jpg?text=${Uri.encode(text)}"
        else
            "https://placehold.co/${width}x${height}.jpg"

        val url = URL(imageUrl)
        val connection = url.openConnection() as HttpURLConnection

        connection.connectTimeout = 15000
        connection.readTimeout = 15000
        connection.connect()

        val inputStream = connection.inputStream

        val fileName = "remote_${UUID.randomUUID()}.jpg"

        // create a directory in the Downloads folder
        val downloadsDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "FordLoadImageAssignment")
        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs()
        }
        // create a file in the directory
        val file = File(downloadsDir, fileName)

        // write the input stream to a file
        FileOutputStream(file).use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        inputStream.close()
        connection.disconnect()

        // scan the file so it appears in the gallery
        MediaScannerConnection.scanFile(context, arrayOf(file.absolutePath), null, null)

        // get the dimensions of the image
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeFile(file.absolutePath, options)

        // create an ImageItem object
        ImageItem(
            id = UUID.randomUUID().toString(),
            uri = Uri.fromFile(file),
            name = fileName,
            size = file.length(),
            dateAdded = System.currentTimeMillis() / 1000,
            width = options.outWidth,
            height = options.outHeight,
        )
    }

}

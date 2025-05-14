package com.jamesc.fordloadimageassignment.utils

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.exifinterface.media.ExifInterface
import com.example.imageviewer.models.ImageItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ImageLoader(private val context: Context) {
    suspend fun loadImagesFromDevice(): List<ImageItem> = withContext(Dispatchers.IO) {
        val images = mutableListOf<ImageItem>()

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT,
            MediaStore.Images.Media.MIME_TYPE
        )
        //as told me to do this
        val query = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null
            )
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        try {
            if (query != null && query.moveToFirst()) {
                do {
                    val idColumn = query.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                    val nameColumn =
                        query.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                    val sizeColumn = query.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
                    val dateAddedColumn =
                        query.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
                    val widthColumn = query.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
                    val heightColumn = query.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)
                    val mimeTypeColumn =
                        query.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)

                    val id = query.getLong(idColumn)
                    images.add(
                        ImageItem(
                            id = id.toString(),
                            uri = ContentUris.withAppendedId(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id
                            ),
                            name = query.getString(nameColumn),
                            size = query.getLong(sizeColumn),
                            dateAdded = query.getLong(dateAddedColumn),
                            width = query.getInt(widthColumn),
                            height = query.getInt(heightColumn),
                        )
                    )
                } while (query.moveToNext())
            }
        } finally {
            query?.close()
        }

        images
    }


}
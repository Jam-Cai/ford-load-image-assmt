package com.example.fordloadimageassignment.models

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImageItem(
    val id: String,
    val uri: Uri,
    val name: String,
    val size: Long = 0,
    val dateAdded: Long = 0,
    val width: Int = 0,
    val height: Int = 0,
) : Parcelable
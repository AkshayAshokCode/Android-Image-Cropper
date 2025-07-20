package com.akshayashokcode.imagepicker.model

import android.graphics.Bitmap
import android.net.Uri

sealed class ImagePickerResult {
    data class Success(val uri: Uri) : ImagePickerResult()
    data class SuccessWithBitmap(val uri: Uri, val bitmap: Bitmap) : ImagePickerResult()
    data object Cancelled : ImagePickerResult()
    data class Error(val message: String) : ImagePickerResult()
}
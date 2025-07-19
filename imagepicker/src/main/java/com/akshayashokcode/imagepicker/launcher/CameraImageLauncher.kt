package com.akshayashokcode.imagepicker.launcher

import android.content.Context
import android.net.Uri
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.contract.ActivityResultContracts
import com.akshayashokcode.imagepicker.util.FileUtils
import com.akshayashokcode.imagepicker.util.ImageOrientationUtils

class CameraImageLauncher(
    private val context: Context,
    caller: ActivityResultCaller,
    private val callback: (ImagePickerResult) -> Unit
) {

    private var tempImageUri: Uri? = null

    private val launcher = caller.registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && tempImageUri != null) {
            // Fix EXIF rotation if needed
            val rotatedBitmap = ImageOrientationUtils.getOrientedBitmap(context.contentResolver, tempImageUri!!)
            if (rotatedBitmap != null) {
                callback(ImagePickerResult.SuccessWithBitmap(uri = tempImageUri!!, bitmap = rotatedBitmap))
            } else {
                callback(ImagePickerResult.Error("Failed to decode or rotate image"))
            }
        } else {
            callback(ImagePickerResult.Cancelled)
            tempImageUri?.let { FileUtils.deleteTempFile(context, it) }
        }
    }

    fun launch() {
        tempImageUri = FileUtils.createTempImageUri(context)
        if (tempImageUri != null) {
            launcher.launch(tempImageUri)
        } else {
            callback(ImagePickerResult.Error("Unable to create temporary image file"))
        }
    }
}
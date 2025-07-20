package com.akshayashokcode.imagepicker.launcher

import android.content.Context
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.akshayashokcode.imagepicker.model.ImagePickerResult

class GalleryImageLauncher(
    private val context: Context,
    caller: ActivityResultCaller,
    private val callback: (ImagePickerResult) -> Unit
) {

    private val launcher: ActivityResultLauncher<String> =
        caller.registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                callback(ImagePickerResult.Success(uri))
            } else {
                callback(ImagePickerResult.Cancelled)
            }
        }

    fun launch() {
        launcher.launch("image/*")
    }
}
package com.akshayashokcode.imagepicker.coordinator

import android.content.Context
import androidx.activity.result.ActivityResultCaller
import com.akshayashokcode.imagepicker.model.ImagePickerException
import com.akshayashokcode.imagepicker.model.ImagePickerResult
import com.akshayashokcode.imagepicker.model.MediaSource
import com.akshayashokcode.imagepicker.picker.CameraImagePicker
import com.akshayashokcode.imagepicker.picker.GalleryImagePicker

internal class ImagePickerCoordinator(
    private val context: Context,
    private val caller: ActivityResultCaller,
    private val source: MediaSource,
    private val onResult: (ImagePickerResult) -> Unit,
    private val onError: ((ImagePickerException) -> Unit)? = null
) {
    private val galleryPicker = GalleryImagePicker(context, caller, onResult, onError)
    private val cameraPicker = CameraImagePicker(context, caller, onResult, onError)

    fun launch() {
        when (source) {
            is MediaSource.Gallery -> galleryPicker.launch()
            is MediaSource.Camera -> cameraPicker.launch()
            is MediaSource.Both -> {
                // 🚧 Future enhancement: show source selection UI
                galleryPicker.launch()
            }
        }
    }
}
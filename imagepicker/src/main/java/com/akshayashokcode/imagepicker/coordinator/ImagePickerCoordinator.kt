package com.akshayashokcode.imagepicker.coordinator

import android.content.Context
import androidx.activity.result.ActivityResultCaller
import com.akshayashokcode.imagepicker.model.ImagePickerResult
import com.akshayashokcode.imagepicker.model.MediaSource
import com.akshayashokcode.imagepicker.picker.CameraImagePicker
import com.akshayashokcode.imagepicker.picker.GalleryImagePicker

internal class ImagePickerCoordinator(
    private val context: Context,
    private val caller: ActivityResultCaller,
    private val source: MediaSource,
    private val callback: (ImagePickerResult) -> Unit
) {
    private val galleryPicker = GalleryImagePicker(context, caller, callback)
    private val cameraPicker = CameraImagePicker(context, caller, callback)

    fun launch() {
        when (source) {
            is MediaSource.Gallery -> galleryPicker.launch()
            is MediaSource.Camera -> cameraPicker.launch()
            is MediaSource.Both -> {
                // TODO: Show dialog or selection sheet (in future ticket)
                galleryPicker.launch()
            }
        }
    }
}
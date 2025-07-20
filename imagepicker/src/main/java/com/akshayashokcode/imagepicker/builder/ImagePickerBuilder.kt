package com.akshayashokcode.imagepicker.builder

import android.content.Context
import android.net.Uri
import com.akshayashokcode.imagepicker.model.ImageSource

class ImagePickerBuilder(private val context: Context) {

    private var source: ImageSource = ImageSource.GALLERY
    private var shouldCrop: Boolean = false
    private var onResult: ((Uri) -> Unit)? = null
    private var onError: ((Throwable) -> Unit)? = null

    fun source(source: ImageSource) = apply {
        this.source = source
    }

    fun crop(enable: Boolean) = apply {
        this.shouldCrop = enable
    }

    fun onResult(callback: (Uri) -> Unit) = apply {
        this.onResult = callback
    }

    fun onError(callback: (Throwable) -> Unit) = apply {
        this.onError = callback
    }

    fun launch() {
        when (source) {
            ImageSource.GALLERY -> {
                // Launch Gallery Picker here (to be plugged in from GalleryImageLauncher)
                onError?.invoke(UnsupportedOperationException("Gallery launch not yet implemented"))
            }
            ImageSource.CAMERA -> {
                // Launch Camera Picker here (to be plugged in from CameraImageLauncher)
                onError?.invoke(UnsupportedOperationException("Camera launch not yet implemented"))
            }
            ImageSource.BOTH -> {
                // Optional: implement UI chooser between Gallery and Camera
                onError?.invoke(UnsupportedOperationException("Chooser not yet implemented"))
            }
        }
    }

    // Internal helpers (optional for future extensions)
    internal fun getCropEnabled(): Boolean = shouldCrop
    internal fun getOnResult(): ((Uri) -> Unit)? = onResult
    internal fun getOnError(): ((Throwable) -> Unit)? = onError
}
package com.akshayashokcode.imagepicker.builder

import android.content.Context
import android.net.Uri
import androidx.activity.result.ActivityResultCaller
import com.akshayashokcode.imagepicker.coordinator.ImagePickerCoordinator
import com.akshayashokcode.imagepicker.model.ImagePickerException
import com.akshayashokcode.imagepicker.model.ImagePickerResult
import com.akshayashokcode.imagepicker.model.MediaSource

class ImagePickerBuilder(
    private val context: Context,
    private val caller: ActivityResultCaller
) {
    private var source: MediaSource = MediaSource.Gallery
    private var crop: Boolean = false // Reserved for future integration
    private var onResult: ((ImagePickerResult) -> Unit)? = null
    private var onError: ((ImagePickerException) -> Unit)? = null

    companion object {
        /**
         * Entry point to start building an image picker flow.
         *
         * @param context Application or activity context
         * @param caller ActivityResultCaller (Activity or Fragment)
         */
        fun with(context: Context, caller: ActivityResultCaller): ImagePickerBuilder {
            return ImagePickerBuilder(context, caller)
        }
    }

    /**
     * Set the image source (GALLERY, CAMERA, or BOTH).
     */
    fun source(source: MediaSource): ImagePickerBuilder = apply {
        this.source = source
    }

    /**
     * Enable cropping after selection (optional feature, not yet active).
     */
    fun crop(enable: Boolean): ImagePickerBuilder = apply {
        this.crop = enable
    }

    /**
     * Callback to receive the picker result.
     */
    fun onResult(callback: (ImagePickerResult) -> Unit): ImagePickerBuilder = apply {
        this.onResult = callback
    }

    /**
     * Optional error callback to receive specific error cases.
     */
    fun onError(callback: (ImagePickerException) -> Unit): ImagePickerBuilder = apply {
        this.onError = callback
    }

    /**
     * Launch the image picker with the current configuration.
     */
    fun launch() {
        requireNotNull(onResult) {
            "You must provide a result callback using onResult()"
        }

        val coordinator = ImagePickerCoordinator(
            context = context,
            caller = caller,
            source = source,
            onResult = onResult!!,
            onError = onError
        )

        coordinator.launch()
    }
}
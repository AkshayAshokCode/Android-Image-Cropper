package com.akshayashokcode.imagepicker.builder

import android.content.Context
import android.net.Uri
import androidx.activity.result.ActivityResultCaller
import com.akshayashokcode.imagepicker.coordinator.ImagePickerCoordinator
import com.akshayashokcode.imagepicker.model.ImagePickerResult
import com.akshayashokcode.imagepicker.model.MediaSource

class ImagePickerBuilder(
    private val context: Context,
    private val caller: ActivityResultCaller
) {
    private var source: MediaSource = MediaSource.Gallery
    private var crop: Boolean = false // Not used yet, but reserved for future integration
    private var onResult: ((ImagePickerResult) -> Unit)? = null

    companion object {
        /**
         * Initialize the builder with context and ActivityResultCaller (Activity or Fragment).
         */
        fun with(context: Context, caller: ActivityResultCaller): ImagePickerBuilder {
            return ImagePickerBuilder(context, caller)
        }
    }

    /**
     * Set the media source: Gallery, Camera or Both
     */
    fun source(source: MediaSource): ImagePickerBuilder = apply {
        this.source = source
    }

    /**
     * Enable or disable cropping after selection (coming soon).
     */
    fun crop(enable: Boolean): ImagePickerBuilder = apply {
        this.crop = enable
    }

    /**
     * Set the callback to receive result.
     */
    fun onResult(callback: (ImagePickerResult) -> Unit): ImagePickerBuilder = apply {
        this.onResult = callback
    }

    /**
     * Launch the picker based on configured options.
     */
    fun launch() {
        requireNotNull(onResult) { "You must provide a result callback using onResult()" }

        val coordinator = ImagePickerCoordinator(
            context = context,
            caller = caller,
            source = source,
            callback = onResult!!
        )

        coordinator.launch()
    }
}
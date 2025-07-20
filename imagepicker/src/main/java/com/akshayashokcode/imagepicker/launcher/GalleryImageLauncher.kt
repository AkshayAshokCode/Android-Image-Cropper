package com.akshayashokcode.imagepicker.launcher

import android.content.ActivityNotFoundException
import android.content.Context
import android.net.Uri
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.contract.ActivityResultContracts
import com.akshayashokcode.imagepicker.model.ImagePickerException
import com.akshayashokcode.imagepicker.model.ImagePickerResult
import com.akshayashokcode.imagepicker.util.AppAvailabilityUtils

class GalleryImageLauncher(
    private val context: Context,
    caller: ActivityResultCaller,
    private val callback: (ImagePickerResult) -> Unit,
    private val onError: ((ImagePickerException) -> Unit)? = null
) {

    private val launcher =
        caller.registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                callback(ImagePickerResult.Success(uri))
            } else {
                callback(ImagePickerResult.Cancelled)
            }
        }

    fun launch() {
        if (!AppAvailabilityUtils.isGalleryAvailable(context)) {
            onError?.invoke(ImagePickerException.AppNotFound)
            callback(ImagePickerResult.Error("No gallery app found to select image"))
            return
        }

        try {
            launcher.launch("image/*")
        } catch (e: ActivityNotFoundException) {
            onError?.invoke(ImagePickerException.IntentFailed)
            callback(ImagePickerResult.Error("No gallery found to handle image picking"))
        } catch (e: Exception) {
            onError?.invoke(ImagePickerException.Unknown("Unexpected error: ${e.message}"))
            callback(ImagePickerResult.Error("Unexpected error occurred during image selection"))
        }
    }
}
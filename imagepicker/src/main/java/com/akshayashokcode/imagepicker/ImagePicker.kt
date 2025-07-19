package com.akshayashokcode.imagepicker

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

class ImagePicker(private val activity: ComponentActivity) {

    private var onImagePicked: ((Uri?) -> Unit)? = null

    private val galleryLauncher: ActivityResultLauncher<String> =
        activity.registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            onImagePicked?.invoke(uri)
        }

    fun pickImageFromGallery(callback: (Uri?) -> Unit) {
        onImagePicked = callback
        galleryLauncher.launch("image/*")
    }
}
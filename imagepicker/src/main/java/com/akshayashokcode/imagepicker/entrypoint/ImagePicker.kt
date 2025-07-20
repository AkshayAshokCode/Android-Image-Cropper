package com.akshayashokcode.imagepicker.entrypoint

import android.content.Context
import com.akshayashokcode.imagepicker.builder.ImagePickerBuilder

object ImagePicker {
    fun with(context: Context): ImagePickerBuilder {
        return ImagePickerBuilder(context)
    }
}
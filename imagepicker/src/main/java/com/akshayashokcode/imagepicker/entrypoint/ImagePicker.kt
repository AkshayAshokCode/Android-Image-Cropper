package com.akshayashokcode.imagepicker.entrypoint

import android.content.Context
import androidx.activity.result.ActivityResultCaller
import com.akshayashokcode.imagepicker.builder.ImagePickerBuilder

object ImagePicker {
    fun with(context: Context, caller: ActivityResultCaller): ImagePickerBuilder {
        return ImagePickerBuilder(context, caller)
    }
}
package com.akshayashokcode.imagecropper.internal

import android.graphics.RectF
import android.os.Parcel
import android.os.Parcelable
import android.view.View.BaseSavedState

internal class CropperSavedState : BaseSavedState {
    var cropLeft = 0f
    var cropTop = 0f
    var cropRight = 0f
    var cropBottom = 0f

    constructor(superState: Parcelable?) : super(superState)

    constructor(parcel: Parcel) : super(parcel) {
        cropLeft = parcel.readFloat()
        cropTop = parcel.readFloat()
        cropRight = parcel.readFloat()
        cropBottom = parcel.readFloat()
    }

    constructor(superState: Parcelable?, cropRect: RectF) : this(superState) {
        cropLeft = cropRect.left
        cropTop = cropRect.top
        cropRight = cropRect.right
        cropBottom = cropRect.bottom
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeFloat(cropLeft)
        parcel.writeFloat(cropTop)
        parcel.writeFloat(cropRight)
        parcel.writeFloat(cropBottom)
    }

    companion object CREATOR : Parcelable.Creator<CropperSavedState> {
        override fun createFromParcel(parcel: Parcel) = CropperSavedState(parcel)
        override fun newArray(size: Int): Array<CropperSavedState?> = arrayOfNulls(size)
    }
}

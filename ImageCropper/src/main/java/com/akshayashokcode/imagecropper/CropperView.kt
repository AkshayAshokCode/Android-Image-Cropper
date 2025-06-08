package com.akshayashokcode.imagecropper

import android.content.Context
import android.graphics.*
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.akshayashokcode.imagecropper.internal.CropOverlayDrawer
import com.akshayashokcode.imagecropper.internal.CropTouchHandler
import com.akshayashokcode.imagecropper.internal.CropperSavedState

class CropperView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var bitmap: Bitmap? = null
    private val matrix = Matrix()
    private val inverseMatrix = Matrix()
    private val cropRect = RectF()
    private val overlayDrawer = CropOverlayDrawer()
    private val touchHandler = CropTouchHandler(cropRect)

    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var currentTouch = CropTouchHandler.Area.NONE

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        bitmap?.let {
            canvas.drawBitmap(it, matrix, overlayDrawer.bitmapPaint)
        }

        overlayDrawer.drawOverlay(canvas, cropRect, width.toFloat(), height.toFloat())
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                currentTouch = touchHandler.detectTouchArea(x, y)
                lastTouchX = x
                lastTouchY = y
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                val dx = x - lastTouchX
                val dy = y - lastTouchY
                touchHandler.updateCropRect(currentTouch, dx, dy)
                constrainCropRectToImageBounds()
                lastTouchX = x
                lastTouchY = y
                invalidate()
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                currentTouch = CropTouchHandler.Area.NONE
            }
        }
        return true
    }

    fun setImageBitmap(bmp: Bitmap) {
        bitmap = bmp
        matrix.reset()

        if (width == 0 || height == 0) {
            post { setImageBitmap(bmp) }
            return
        }

        val scale = minOf(width.toFloat() / bmp.width, height.toFloat() / bmp.height)
        val dx = (width - bmp.width * scale) / 2
        val dy = (height - bmp.height * scale) / 2

        matrix.postScale(scale, scale)
        matrix.postTranslate(dx, dy)

        val bitmapRect = RectF(0f, 0f, bmp.width.toFloat(), bmp.height.toFloat())
        matrix.mapRect(bitmapRect)
        cropRect.set(bitmapRect)

        invalidate()
    }

    fun getCroppedImage(): Bitmap? {
        val bmp = bitmap ?: return null
        if (bmp.isRecycled) return null

        matrix.invert(inverseMatrix)
        val mappedRect = RectF(cropRect).apply { inverseMatrix.mapRect(this) }

        val srcLeft = mappedRect.left.toInt().coerceIn(0, bmp.width - 1)
        val srcTop = mappedRect.top.toInt().coerceIn(0, bmp.height - 1)
        val srcRight = mappedRect.right.toInt().coerceIn(srcLeft + 1, bmp.width)
        val srcBottom = mappedRect.bottom.toInt().coerceIn(srcTop + 1, bmp.height)

        val srcWidth = srcRight - srcLeft
        val srcHeight = srcBottom - srcTop

        return if (srcWidth > 0 && srcHeight > 0)
            Bitmap.createBitmap(bmp, srcLeft, srcTop, srcWidth, srcHeight)
        else null
    }

    private fun constrainCropRectToImageBounds() {
        val bounds = bitmap?.let {
            RectF(0f, 0f, it.width.toFloat(), it.height.toFloat()).apply {
                matrix.mapRect(this)
            }
        } ?: return

        val dx = when {
            cropRect.left < bounds.left -> bounds.left - cropRect.left
            cropRect.right > bounds.right -> bounds.right - cropRect.right
            else -> 0f
        }
        val dy = when {
            cropRect.top < bounds.top -> bounds.top - cropRect.top
            cropRect.bottom > bounds.bottom -> bounds.bottom - cropRect.bottom
            else -> 0f
        }
        cropRect.offset(dx, dy)
    }

    override fun onSaveInstanceState(): Parcelable {
        return CropperSavedState(super.onSaveInstanceState(), cropRect)
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is CropperSavedState) {
            super.onRestoreInstanceState(state.superState)
            cropRect.set(state.cropLeft, state.cropTop, state.cropRight, state.cropBottom)
            invalidate()
        } else {
            super.onRestoreInstanceState(state)
        }
    }
}

package com.akshayashokcode.imagecropper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.toColorInt
import kotlin.math.abs
import kotlin.math.min

class CropperView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var bitmap: Bitmap? = null
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val matrix = Matrix()
    private val inverseMatrix = Matrix()

    private var cropRect = RectF()
    private var lastTouchX = 0f
    private var lastTouchY = 0f

    private val touchThreshold = 40f
    private val minCropSize = 200f

    private enum class TouchArea {
        NONE, MOVE, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
    }

    private var currentTouch = TouchArea.NONE

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        val size = min(w, h) * 0.8f
        val left = (w - size) / 2f
        val top = (h - size) / 2f
        cropRect.set(left, top, left + size, top + size)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                currentTouch = detectTouchArea(x, y)
                lastTouchX = x
                lastTouchY = y
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                val dx = x - lastTouchX
                val dy = y - lastTouchY

                when (currentTouch) {
                    TouchArea.MOVE -> {
                        cropRect.offset(dx, dy)
                        constrainCropRectToBounds()
                    }

                    TouchArea.TOP_LEFT -> {
                        cropRect.left =
                            (cropRect.left + dx).coerceAtMost(cropRect.right - minCropSize)
                        cropRect.top =
                            (cropRect.top + dy).coerceAtMost(cropRect.bottom - minCropSize)
                    }

                    TouchArea.TOP_RIGHT -> {
                        cropRect.right =
                            (cropRect.right + dx).coerceAtLeast(cropRect.left + minCropSize)
                        cropRect.top =
                            (cropRect.top + dy).coerceAtMost(cropRect.bottom - minCropSize)
                    }

                    TouchArea.BOTTOM_LEFT -> {
                        cropRect.left =
                            (cropRect.left + dx).coerceAtMost(cropRect.right - minCropSize)
                        cropRect.bottom =
                            (cropRect.bottom + dy).coerceAtLeast(cropRect.top + minCropSize)
                    }

                    TouchArea.BOTTOM_RIGHT -> {
                        cropRect.right =
                            (cropRect.right + dx).coerceAtLeast(cropRect.left + minCropSize)
                        cropRect.bottom =
                            (cropRect.bottom + dy).coerceAtLeast(cropRect.top + minCropSize)
                    }

                    else -> {}
                }

                constrainCropRectToBounds()
                lastTouchX = x
                lastTouchY = y
                invalidate()
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                currentTouch = TouchArea.NONE
            }
        }

        return true
    }

    private fun detectTouchArea(x: Float, y: Float): TouchArea {
        return when {
            isNear(x, y, cropRect.left, cropRect.top) -> TouchArea.TOP_LEFT
            isNear(x, y, cropRect.right, cropRect.top) -> TouchArea.TOP_RIGHT
            isNear(x, y, cropRect.left, cropRect.bottom) -> TouchArea.BOTTOM_LEFT
            isNear(x, y, cropRect.right, cropRect.bottom) -> TouchArea.BOTTOM_RIGHT
            cropRect.contains(x, y) -> TouchArea.MOVE
            else -> TouchArea.NONE
        }
    }

    private fun isNear(x1: Float, y1: Float, x2: Float, y2: Float): Boolean {
        return abs(x1 - x2) < touchThreshold && abs(y1 - y2) < touchThreshold
    }

    private fun constrainCropRectToBounds() {
        cropRect.left = cropRect.left.coerceIn(0f, width.toFloat() - minCropSize)
        cropRect.top = cropRect.top.coerceIn(0f, height.toFloat() - minCropSize)
        cropRect.right = cropRect.right.coerceIn(minCropSize, width.toFloat())
        cropRect.bottom = cropRect.bottom.coerceIn(minCropSize, height.toFloat())
    }

    private val overlayPaint = Paint().apply { color = "#B0000000".toColorInt() }
    private val cornerStrokeWidth = 10f
    private val cornerPaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        this.strokeWidth = cornerStrokeWidth
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        bitmap?.let {
            canvas.drawBitmap(it, matrix, paint)
        }

        // Draw outside overlay
        canvas.drawRect(0f, 0f, width.toFloat(), cropRect.top, overlayPaint)
        canvas.drawRect(0f, cropRect.bottom, width.toFloat(), height.toFloat(), overlayPaint)
        canvas.drawRect(0f, cropRect.top, cropRect.left, cropRect.bottom, overlayPaint)
        canvas.drawRect(
            cropRect.right,
            cropRect.top,
            width.toFloat(),
            cropRect.bottom,
            overlayPaint
        )

        // Draw crop rect
        paint.style = Paint.Style.STROKE
        paint.color = Color.WHITE
        paint.strokeWidth = 4f
        canvas.drawRect(cropRect, paint)

        // Draw L-shaped corners
        val handleLength = 40f


        // Top-left
        canvas.drawLine(
            cropRect.left,
            cropRect.top,
            cropRect.left + handleLength,
            cropRect.top,
            cornerPaint
        )
        canvas.drawLine(
            cropRect.left,
            cropRect.top,
            cropRect.left,
            cropRect.top + handleLength,
            cornerPaint
        )

        // Top-right
        canvas.drawLine(
            cropRect.right,
            cropRect.top,
            cropRect.right - handleLength,
            cropRect.top,
            cornerPaint
        )
        canvas.drawLine(
            cropRect.right,
            cropRect.top,
            cropRect.right,
            cropRect.top + handleLength,
            cornerPaint
        )

        // Bottom-left
        canvas.drawLine(
            cropRect.left,
            cropRect.bottom,
            cropRect.left + handleLength,
            cropRect.bottom,
            cornerPaint
        )
        canvas.drawLine(
            cropRect.left,
            cropRect.bottom,
            cropRect.left,
            cropRect.bottom - handleLength,
            cornerPaint
        )

        // Bottom-right
        canvas.drawLine(
            cropRect.right,
            cropRect.bottom,
            cropRect.right - handleLength,
            cropRect.bottom,
            cornerPaint
        )
        canvas.drawLine(
            cropRect.right,
            cropRect.bottom,
            cropRect.right,
            cropRect.bottom - handleLength,
            cornerPaint
        )

    }

    fun setImageBitmap(bmp: Bitmap) {
        bitmap = bmp
        matrix.reset()

        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()
        val imageWidth = bmp.width.toFloat()
        val imageHeight = bmp.height.toFloat()

        val scale = min(viewWidth / imageWidth, viewHeight / imageHeight)
        val dx = (viewWidth - imageWidth * scale) / 2
        val dy = (viewHeight - imageHeight * scale) / 2

        matrix.postScale(scale, scale)
        matrix.postTranslate(dx, dy)

        invalidate()
    }

    fun getCroppedImage(): Bitmap? {
        val bmp = bitmap ?: return null
        matrix.invert(inverseMatrix)

        val mappedRect = RectF(cropRect)
        inverseMatrix.mapRect(mappedRect)

        val srcLeft = mappedRect.left.toInt().coerceIn(0, bmp.width)
        val srcTop = mappedRect.top.toInt().coerceIn(0, bmp.height)
        val srcWidth = mappedRect.width().toInt().coerceIn(0, bmp.width - srcLeft)
        val srcHeight = mappedRect.height().toInt().coerceIn(0, bmp.height - srcTop)

        return Bitmap.createBitmap(bmp, srcLeft, srcTop, srcWidth, srcHeight)
    }
}
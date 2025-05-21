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
import android.view.ScaleGestureDetector
import android.view.View

class CropperView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var bitmap: Bitmap? = null
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val cropRect = RectF()

    // Gesture support
    private var scaleDetector: ScaleGestureDetector
    private var scaleFactor = 1f
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var posX = 0f
    private var posY = 0f

    init {
        scaleDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                scaleFactor *= detector.scaleFactor
                scaleFactor = scaleFactor.coerceIn(0.5f, 5.0f)
                invalidate()
                return true
            }
        })
    }

    fun setImageBitmap(bmp: Bitmap) {
        bitmap = bmp
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        bitmap?.let {
            canvas.save()
            canvas.translate(posX, posY)
            canvas.scale(scaleFactor, scaleFactor)

            val left = (width - it.width) / 2f
            val top = (height - it.height) / 2f
            canvas.drawBitmap(it, left, top, paint)
            canvas.restore()
        }

        // Draw crop window (fixed centered square for now)
        val side = width.coerceAtMost(height) * 0.6f
        cropRect.set(
            (width - side) / 2,
            (height - side) / 2,
            (width + side) / 2,
            (height + side) / 2
        )

        paint.style = Paint.Style.STROKE
        paint.color = Color.WHITE
        paint.strokeWidth = 4f
        canvas.drawRect(cropRect, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(event)

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchX = event.x
                lastTouchY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = event.x - lastTouchX
                val dy = event.y - lastTouchY
                posX += dx
                posY += dy
                invalidate()
                lastTouchX = event.x
                lastTouchY = event.y
            }
        }

        return true
    }

    fun getCroppedImage(): Bitmap? {
        val srcBitmap = bitmap ?: return null

        // Inverse of the canvas transformations
        val inverseMatrix = Matrix()
        val matrix = Matrix()
        matrix.postTranslate(posX, posY)
        matrix.postScale(scaleFactor, scaleFactor, width / 2f, height / 2f)
        if (!matrix.invert(inverseMatrix)) return null

        // Map cropRect from view space to bitmap space
        val mappedRect = RectF()
        inverseMatrix.mapRect(mappedRect, cropRect)

        // Clamp crop rect inside bitmap bounds
        mappedRect.intersect(0f, 0f, srcBitmap.width.toFloat(), srcBitmap.height.toFloat())

        // Convert to Int coordinates
        val left = mappedRect.left.toInt().coerceAtLeast(0)
        val top = mappedRect.top.toInt().coerceAtLeast(0)
        val width = mappedRect.width().toInt().coerceAtMost(srcBitmap.width - left)
        val height = mappedRect.height().toInt().coerceAtMost(srcBitmap.height - top)

        if (width <= 0 || height <= 0) return null

        return Bitmap.createBitmap(srcBitmap, left, top, width, height)
    }
}
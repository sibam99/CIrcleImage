package com.metafurygames.mycircleimageview

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class MyCircleImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        isAntiAlias = true
    }

    private var borderColor: Int = Color.BLACK

    init {
        val typedArray = context.obtainStyledAttributes(attrs, com.example.app.R.styleable.MyCircleImageView, defStyleAttr, 0)
        val borderColor = typedArray.getColor(com.example.app.R.styleable.MyCircleImageView_borderColor, Color.BLACK)
        val borderWidth = typedArray.getDimension(com.example.app.R.styleable.MyCircleImageView_borderWidth, 0f)
        typedArray.recycle()

        paint.strokeWidth = borderWidth
        paint.color = borderColor
    }

    override fun onDraw(canvas: Canvas) {
        val drawable = drawable ?: return
        val bitmap = getBitmapFromDrawable(drawable)

        // Calculate the radius
        val radius = Math.min(width / 2f, height / 2f)

        // Create a shader with the bitmap
        val shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint.shader = shader

        // Calculate the scaling
        val scale: Float
        val dx: Float
        val dy: Float
        val bitmapWidth = bitmap.width
        val bitmapHeight = bitmap.height

        if (bitmapWidth * height > width * bitmapHeight) {
            scale = height.toFloat() / bitmapHeight.toFloat()
            dx = (width - bitmapWidth * scale) * 0.5f
            dy = 0f
        } else {
            scale = width.toFloat() / bitmapWidth.toFloat()
            dx = 0f
            dy = (height - bitmapHeight * scale) * 0.5f
        }

        // Apply the shader matrix
        val matrix = Matrix().apply {
            setScale(scale, scale)
            postTranslate(dx, dy)
        }
        paint.shader.setLocalMatrix(matrix)

        // Draw the circle with the image
        canvas.drawCircle(width / 2f, height / 2f, radius, paint)

        // Draw the border if needed
        if (paint.strokeWidth > 0) {
            paint.shader = null
            paint.style = Paint.Style.STROKE
            paint.color = borderColor
            canvas.drawCircle(width / 2f, height / 2f, radius - paint.strokeWidth / 2, paint)
        }
    }



    private fun getBitmapFromDrawable(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

}

package com.example.EasyQar.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import androidx.core.content.ContextCompat
import com.example.EasyQar.R

fun createBrandedQrBitmap(
    qrBitmap: Bitmap,
    context: Context,
    logoResId: Int,
    appName: String
): Bitmap {
    val logo = BitmapFactory.decodeResource(context.resources, logoResId)

    val qrWidth = qrBitmap.width
    val qrHeight = qrBitmap.height

    val logoSize = 80
    val textSizePx = 40f
    val spacing = 16f

    // dp'den px'e çevir
    val dpToPx = { dp: Int -> (dp * context.resources.displayMetrics.density).toInt() }
    val verticalPadding = dpToPx(8)

    // Logo + yazı için yukarıya ekstra alan
    val contentHeight = maxOf(logoSize, textSizePx.toInt()) + 2 * verticalPadding
    val totalHeight = qrHeight + contentHeight

    val resultBitmap = Bitmap.createBitmap(qrWidth, totalHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(resultBitmap)

    canvas.drawColor(Color.WHITE)

    // Yazı ve logoyu üst tarafa çiz
    val scaledLogo = Bitmap.createScaledBitmap(logo, logoSize, logoSize, true)

    val paint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.primaryBlue)
        textSize = textSizePx
        textAlign = Paint.Align.LEFT
        isAntiAlias = true
        isSubpixelText = true
        isLinearText = true
        setHinting(Paint.HINTING_ON)
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }



    val textWidth = paint.measureText(appName)
    val totalContentWidth = logoSize + spacing + textWidth
    val startX = (qrWidth - totalContentWidth) / 2f
    val startY = verticalPadding
    canvas.drawBitmap(scaledLogo, startX, startY.toFloat(), null)

    val textX = startX + logoSize + spacing
    val textY = startY + logoSize / 1.5f
    canvas.drawText(appName, textX.toFloat(), textY.toFloat(), paint)

    // QR kodu alta çiz
    canvas.drawBitmap(qrBitmap, 0f, contentHeight.toFloat(), null)

    return resultBitmap
}




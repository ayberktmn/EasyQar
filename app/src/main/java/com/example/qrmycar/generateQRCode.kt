package com.example.qrmycar

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

fun generateQRCode(text: String): Bitmap {
    val writer = QRCodeWriter()
    val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 512, 512)
    val width = bitMatrix.width
    val height = bitMatrix.height
    val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
    for (x in 0 until width) {
        for (y in 0 until height) {
            bmp.setPixel(x, y, if (bitMatrix.get(x, y)) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
        }
    }
    return bmp
}

fun generateUniqueQRCode(plate: String, email: String): Bitmap {
    val uniqueData = "$plate|$email"
    return generateQRCode(uniqueData)
}
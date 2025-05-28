package com.example.qrmycar.utils

import android.graphics.Bitmap
import android.util.Base64
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

// QR kod oluşturma – renkleri parametre olarak alır
fun generateQRCode(
    text: String,
    qrColor: Int,
    backgroundColor: Int
): Bitmap {
    val writer = QRCodeWriter()
    val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 512, 512)
    val width = bitMatrix.width
    val height = bitMatrix.height
    val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    for (x in 0 until width) {
        for (y in 0 until height) {
            bmp.setPixel(x, y, if (bitMatrix.get(x, y)) qrColor else backgroundColor)
        }
    }
    return bmp
}

// Plaka ve email ile özel QR kod (isteğe bağlı kullanım)
fun generateUniqueQRCode(
    plate: String,
    email: String,
    qrColor: Int,
    backgroundColor: Int
): Bitmap {
    val uniqueData = "$plate|$email"
    return generateQRCode(uniqueData, qrColor, backgroundColor)
}

// Firebase kullanıcı UID'sine özel QR kod
fun generateUserQRCode(
    uid: String,
    qrColor: Int,
    backgroundColor: Int
): Bitmap {
    val encoded = Base64.encodeToString(uid.toByteArray(), Base64.NO_WRAP)
    return generateQRCode(encoded, qrColor, backgroundColor)
}

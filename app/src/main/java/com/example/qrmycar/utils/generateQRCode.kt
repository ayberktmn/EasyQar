package com.example.qrmycar.utils

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Base64
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
            bmp.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
        }
    }
    return bmp
}

// Eski yöntem (isteğe bağlı kullanılabilir)
fun generateUniqueQRCode(plate: String, email: String): Bitmap {
    val uniqueData = "$plate|$email"
    return generateQRCode(uniqueData)
}

// ✅ Firebase kullanıcı UID'sine özel QR kod
fun generateUserQRCode(uid: String): Bitmap {
    // UID'yi base64 ile encode ederek daha okunmaz hale getirebiliriz (isteğe bağlı)
    val encoded = Base64.encodeToString(uid.toByteArray(), Base64.NO_WRAP)
   /// val uid = "$uid"
    return generateQRCode(encoded)
}

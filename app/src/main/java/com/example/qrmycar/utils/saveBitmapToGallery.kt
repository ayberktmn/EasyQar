package com.example.qrmycar.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

fun saveBitmapToGallery(context: Context, bitmap: Bitmap, fileName: String = "qr_code") {
    val resolver = context.contentResolver
    val imageCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    } else {
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }

    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "${fileName}_${System.currentTimeMillis()}.png")
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        put(MediaStore.Images.Media.WIDTH, bitmap.width)
        put(MediaStore.Images.Media.HEIGHT, bitmap.height)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
    }

    val imageUri = resolver.insert(imageCollection, contentValues)

    imageUri?.let { uri ->
        resolver.openOutputStream(uri).use { outputStream: OutputStream? ->
            outputStream?.let { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.clear()
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(uri, contentValues, null, null)
        }
        Toast.makeText(context, "QR kodu indirildi", Toast.LENGTH_SHORT).show()
    } ?: run {
        Toast.makeText(context, "QR kodu indirilemedi", Toast.LENGTH_SHORT).show()
    }
}

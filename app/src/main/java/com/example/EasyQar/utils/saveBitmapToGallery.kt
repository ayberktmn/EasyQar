package com.example.EasyQar.utils

import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

fun saveBitmapToGallery(context: Context, bitmap: Bitmap) {
    val filename = "easyqar_qr_${System.currentTimeMillis()}.png"
    val fos: OutputStream?
    var savedUri: Uri? = null

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/EasyQarQR")
        }

        val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        savedUri = imageUri
        fos = imageUri?.let { resolver.openOutputStream(it) }
    } else {
        val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/EasyQarQR")
        if (!imagesDir.exists()) imagesDir.mkdirs()
        val image = File(imagesDir, filename)
        fos = FileOutputStream(image)
        savedUri = Uri.fromFile(image)
    }

    fos?.use {
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        Toast.makeText(context, "QR kodu galeriye kaydedildi.", Toast.LENGTH_SHORT).show()
    }

    savedUri?.let {
        // Kullanıcıyı yönlendir
        val viewIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(it, "image/*")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

        try {
            context.startActivity(viewIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "Dosya görüntüleyici bulunamadı.", Toast.LENGTH_SHORT).show()
        }
    }
}

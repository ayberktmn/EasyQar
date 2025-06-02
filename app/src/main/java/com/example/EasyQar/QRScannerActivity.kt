package com.example.EasyQar.screens

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Base64
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator

class QRScannerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // QR kod tarayıcıyı başlat
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt("QR kodu okutun")
        integrator.setCameraId(0)  // 0 -> Arka kamera, 1 -> Ön kamera
        integrator.setBeepEnabled(true)
        integrator.setBarcodeImageEnabled(true)
        integrator.initiateScan()
    }

    // Tarama sonucu alındığında
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // QR kod tarayıcı sonucu
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                setResult(Activity.RESULT_CANCELED)
                finish()
            } else {
                val scannedData = result.contents
                // QR kod çözümü (Base64)
                try {
                    val decoded = Base64.decode(scannedData, Base64.DEFAULT)
                    val resultString = String(decoded)
                    val parts = resultString.split("|")
                    val email = parts.getOrNull(0) ?: "bilinmiyor"
                    val plate = parts.getOrNull(1) ?: "bilinmiyor"

                    // QR kodu okunduysa veriyi geri ilet
                    val resultIntent = Intent().apply {
                        putExtra("SCAN_RESULT", resultString)
                    }
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()

                } catch (e: Exception) {
                    setResult(Activity.RESULT_CANCELED)
                    finish()
                }
            }
        }
    }
}

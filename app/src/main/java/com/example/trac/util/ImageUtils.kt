package com.example.trac.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.InputStream

object ImageUtils {
    fun bitmapToBase64(bitmap: Bitmap, quality: Int = 75): String {
        val maxDimension = 600
        val width = bitmap.width
        val height = bitmap.height
        val scale = if (width > maxDimension || height > maxDimension) {
            maxDimension.toFloat() / Math.max(width, height)
        } else {
            1.0f
        }

        val resizedBitmap = if (scale < 1.0f) {
            Bitmap.createScaledBitmap(bitmap, (width * scale).toInt(), (height * scale).toInt(), true)
        } else {
            bitmap
        }

        val outputStream = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        val byteArray = outputStream.toByteArray()
        return "data:image/jpeg;base64," + Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    fun uriToBase64(context: Context, uri: Uri): String? {
        return runCatching {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            originalBitmap?.let { bitmapToBase64(it) }
        }.getOrNull()
    }

    fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
        return runCatching {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            bitmap
        }.getOrNull()
    }

    fun base64ToBitmap(base64Str: String): Bitmap? {
        return runCatching {
            val pureBase64 = if (base64Str.contains(",")) {
                base64Str.substringAfter(",")
            } else {
                base64Str
            }
            val decodedBytes = Base64.decode(pureBase64, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        }.getOrNull()
    }
}

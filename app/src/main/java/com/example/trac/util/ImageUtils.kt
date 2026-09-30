package com.example.trac.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

object ImageUtils {

    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    fun decodeSampledBitmapFromUri(context: Context, uri: Uri, reqWidth: Int = 800, reqHeight: Int = 800): Bitmap? {
        return runCatching {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, options)
            }

            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
            options.inJustDecodeBounds = false

            context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, options)
            }
        }.getOrNull()
    }

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

    suspend fun bitmapToBase64Async(bitmap: Bitmap, quality: Int = 75): String = withContext(Dispatchers.Default) {
        bitmapToBase64(bitmap, quality)
    }

    fun uriToBase64(context: Context, uri: Uri): String? {
        val bitmap = decodeSampledBitmapFromUri(context, uri, 800, 800) ?: return null
        return bitmapToBase64(bitmap)
    }

    suspend fun uriToBase64Async(context: Context, uri: Uri): String? = withContext(Dispatchers.IO) {
        uriToBase64(context, uri)
    }

    fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
        return decodeSampledBitmapFromUri(context, uri, 800, 800)
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


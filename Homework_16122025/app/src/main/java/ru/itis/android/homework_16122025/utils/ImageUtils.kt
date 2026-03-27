package ru.itis.android.homework_16122025.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

object ImageUtils {
    
    suspend fun encodeBitmapToBase64(
        bitmap: Bitmap,
        quality: Int = ImageConstants.COMPRESSION_QUALITY,
        format: Bitmap.CompressFormat = ImageConstants.COMPRESSION_FORMAT
    ): String? = withContext(Dispatchers.IO) {
        try {
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(format, quality, outputStream)
            val byteArray = outputStream.toByteArray()
            outputStream.close()
            Base64.encodeToString(byteArray, Base64.DEFAULT)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun decodeBase64ToBitmap(base64String: String): Bitmap? = withContext(Dispatchers.IO) {
        try {
            if (base64String.isEmpty()) return@withContext null
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun scaleBitmap(
        bitmap: Bitmap,
        maxWidth: Int = ImageConstants.MAX_COVER_WIDTH,
        maxHeight: Int = ImageConstants.MAX_COVER_HEIGHT
    ): Bitmap = withContext(Dispatchers.IO) {
        val width = bitmap.width
        val height = bitmap.height

        if (width <= maxWidth && height <= maxHeight) {
            return@withContext bitmap
        }

        val ratio = minOf(
            maxWidth.toFloat() / width,
            maxHeight.toFloat() / height
        )

        val scaledWidth = (width * ratio).toInt()
        val scaledHeight = (height * ratio).toInt()

        Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true)
    }

    fun isValidBase64(base64String: String): Boolean {
        if (base64String.isEmpty()) return false
        return try {
            Base64.decode(base64String, Base64.DEFAULT)
            true
        } catch (e: Exception) {
            false
        }
    }
}


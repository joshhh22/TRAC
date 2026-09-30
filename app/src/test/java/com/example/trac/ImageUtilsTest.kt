package com.example.trac

import android.graphics.BitmapFactory
import com.example.trac.util.ImageUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ImageUtilsTest {

    @Test
    fun calculateInSampleSize_returnsOne_whenImageWithinBounds() {
        val options = BitmapFactory.Options().apply {
            outWidth = 600
            outHeight = 400
        }
        val sampleSize = ImageUtils.calculateInSampleSize(options, 800, 800)
        assertEquals(1, sampleSize)
    }

    @Test
    fun calculateInSampleSize_scalesDown_whenImageLargerThanTarget() {
        val options = BitmapFactory.Options().apply {
            outWidth = 3200
            outHeight = 2400
        }
        val sampleSize = ImageUtils.calculateInSampleSize(options, 800, 800)
        // 2400 / 2 = 1200 >= 800 (sampleSize = 2), then 1200 / 2 = 600 < 800 -> terminates at 2
        assertEquals(2, sampleSize)
    }

    @Test
    fun base64ToBitmap_returnsNull_onInvalidBase64String() {
        val invalidResult = ImageUtils.base64ToBitmap("invalid_base64_string")
        assertNull(invalidResult)
    }
}

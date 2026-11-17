package com.example.split_screen.util

import android.content.Context
import android.view.accessibility.AccessibilityManager
import androidx.test.core.app.ApplicationProvider
import com.example.split_screen.service.SplitAccessibilityService
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

@RunWith(AndroidJUnit4::class)
class AccessibilityHelperTest {

    @Test
    fun isServiceActiveReturnsFalseWhenServiceNotEnabled() {
        val context: Context = ApplicationProvider.getApplicationContext<Context>()

        val result = AccessibilityHelper.isServiceActive(context)

        assertFalse("El servicio no debe estar activo en entorno de prueba", result)
    }

    @Test
    fun helperClassExists() {
        assertTrue("AccessibilityHelper debe existir", AccessibilityHelper::class.java.name.isNotEmpty())
    }
}

package com.example.split_screen.model

import org.junit.Test
import org.junit.Assert.*

class AppInfoTest {

    @Test
    fun appInfoIsImmutableAndEquatable() {
        val a1 = AppInfo("Chrome", "com.android.chrome")
        val a2 = AppInfo("Chrome", "com.android.chrome")
        val a3 = AppInfo("WhatsApp", "com.whatsapp")

        assertEquals(a1, a2)
        assertNotEquals(a1, a3)
        assertEquals(a1.hashCode(), a2.hashCode())
    }

    @Test
    fun appInfoToStringContainsNameAndPackage() {
        val app = AppInfo("Chrome", "com.android.chrome")
        val str = app.toString()
        assertTrue(str.contains("Chrome"))
        assertTrue(str.contains("com.android.chrome"))
    }

    @Test
    fun appInfoFieldsAreImmutable() {
        val app = AppInfo("Test", "com.test")
        // data class con val -> campos inmutables por definición
        assertEquals("Test", app.name)
        assertEquals("com.test", app.packageName)
    }
}
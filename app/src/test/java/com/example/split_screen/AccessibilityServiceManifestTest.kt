package com.example.split_screen

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class AccessibilityServiceManifestTest {

    private val manifest = File("src/main/AndroidManifest.xml").readText()

    /** Sin esta acción Android no lista el servicio en Ajustes y no se puede activar. */
    @Test
    fun serviceDeclaresAccessibilityServiceAction() {
        assertTrue(manifest.contains("android.accessibilityservice.AccessibilityService\""))
    }

    @Test
    fun serviceRequiresBindPermission() {
        assertTrue(manifest.contains("android.permission.BIND_ACCESSIBILITY_SERVICE"))
    }

    /** Con feedbackType 0 el sistema excluye el servicio de getEnabledAccessibilityServiceList. */
    @Test
    fun configDeclaresFeedbackType() {
        val config = File("src/main/res/xml/accessibility_service_config.xml").readText()
        assertTrue(config.contains("accessibilityFeedbackType=\"feedbackGeneric\""))
    }
}

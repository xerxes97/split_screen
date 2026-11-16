package com.example.split_screen.service

import android.accessibilityservice.AccessibilityService
import org.junit.Test
import org.junit.Assert.*
import java.lang.reflect.Field
import java.lang.reflect.Modifier

class SplitAccessibilityServiceStructureTest {

    @Test
    fun splitAccessibilityServiceExtendsAccessibilityService() {
        assertTrue(
            "SplitAccessibilityService debe heredar de AccessibilityService",
            AccessibilityService::class.java.isAssignableFrom(SplitAccessibilityService::class.java)
        )
    }

    @Test
    fun splitAccessibilityServiceHasGetInstanceMethod() {
        val method = SplitAccessibilityService::class.java.getDeclaredMethod("getInstance")
        assertNotNull("getInstance() debe existir", method)
    }

    @Test
    fun instanceFieldIsPrivate() {
        val instanceField = SplitAccessibilityService::class.java.getDeclaredField("instance")
        assertTrue(
            "El campo 'instance' debe ser private",
            Modifier.isPrivate(instanceField.modifiers)
        )
    }

    @Test
    fun companionObjectIsPrivate() {
        val companionFields = SplitAccessibilityService::class.java.getDeclaredFields()
            .filter { it.name.contains("Companion") }
        assertTrue("Debe existir un campo Companion", companionFields.isNotEmpty())
        companionFields.forEach {
            assertTrue(
                "Companion debe ser private: ${it.name}",
                Modifier.isPrivate(it.modifiers)
            )
        }
    }

    @Test
    fun lifecycleMethodsAreDocumented() {
        val onServiceConnected = SplitAccessibilityService::class.java.getDeclaredMethod("onServiceConnected")
        val onUnbind = SplitAccessibilityService::class.java.getDeclaredMethod("onUnbind", android.content.Intent::class.java)
        val onDestroy = SplitAccessibilityService::class.java.getDeclaredMethod("onDestroy")
        val onAccessibilityEvent = SplitAccessibilityService::class.java.getDeclaredMethod("onAccessibilityEvent", android.view.accessibility.AccessibilityEvent::class.java)
        val onInterrupt = SplitAccessibilityService::class.java.getDeclaredMethod("onInterrupt")

        assertNotNull("onServiceConnected() debe existir", onServiceConnected)
        assertNotNull("onUnbind(intent) debe existir", onUnbind)
        assertNotNull("onDestroy() debe existir", onDestroy)
        assertNotNull("onAccessibilityEvent(event) debe existir", onAccessibilityEvent)
        assertNotNull("onInterrupt() debe existir", onInterrupt)
    }
}

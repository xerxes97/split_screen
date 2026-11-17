package com.example.split_screen.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AccessibilityHelperConnectedTest {

    private val pkg = "com.example.split_screen"
    private val cls = "com.example.split_screen.service.SplitAccessibilityService"

    private fun check(connected: List<Pair<String, String>>) =
        AccessibilityHelper.isServiceConnected(connected, pkg, cls)

    @Test
    fun emptyListReturnsFalse() {
        assertFalse(check(emptyList()))
    }

    @Test
    fun ownServiceConnectedReturnsTrue() {
        assertTrue(check(listOf(pkg to cls)))
    }

    @Test
    fun ownServiceAmongOthersReturnsTrue() {
        assertTrue(
            check(
                listOf(
                    "com.google.android.marvin.talkback" to "com.google.android.marvin.talkback.TalkBackService",
                    pkg to cls
                )
            )
        )
    }

    @Test
    fun otherPackageSameClassReturnsFalse() {
        assertFalse(check(listOf("com.malicious.app" to cls)))
    }

    @Test
    fun samePackageOtherClassReturnsFalse() {
        assertFalse(check(listOf(pkg to "com.example.split_screen.service.OtherService")))
    }
}

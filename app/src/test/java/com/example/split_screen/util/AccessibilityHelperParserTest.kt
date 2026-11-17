package com.example.split_screen.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AccessibilityHelperParserTest {

    private val pkg = "com.example.split_screen"
    private val cls = "com.example.split_screen.service.SplitAccessibilityService"

    private fun check(list: String?) = AccessibilityHelper.isServiceInList(list, pkg, cls)

    @Test
    fun nullOrBlankListReturnsFalse() {
        assertFalse(check(null))
        assertFalse(check(""))
        assertFalse(check("   "))
    }

    @Test
    fun fullClassNameMatches() {
        assertTrue(check("$pkg/$cls"))
    }

    @Test
    fun shortClassNameMatches() {
        assertTrue(check("$pkg/.service.SplitAccessibilityService"))
    }

    @Test
    fun matchesAmongOtherServices() {
        assertTrue(check("com.google.android.marvin.talkback/.TalkBackService:$pkg/$cls"))
    }

    @Test
    fun otherServiceOnlyReturnsFalse() {
        assertFalse(check("com.google.android.marvin.talkback/.TalkBackService"))
    }

    @Test
    fun samePackageDifferentClassReturnsFalse() {
        assertFalse(check("$pkg/.service.OtherService"))
    }

    @Test
    fun sameClassDifferentPackageReturnsFalse() {
        assertFalse(check("com.malicious.app/$cls"))
    }

    @Test
    fun malformedEntryReturnsFalse() {
        assertFalse(check(pkg))
        assertFalse(check("$pkg/$cls/extra"))
    }
}

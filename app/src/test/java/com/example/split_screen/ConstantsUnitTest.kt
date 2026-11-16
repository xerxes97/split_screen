package com.example.split_screen

import org.junit.Test
import org.junit.Assert.*

class ConstantsUnitTest {

    @Test
    fun greetingDefaultNameIsNotEmpty() {
        assertFalse(Constants.Greeting.DEFAULT_NAME.isEmpty())
    }

    @Test
    fun preferenceKeysAreNotEmpty() {
        assertFalse(Constants.Preferences.KEY_SPLIT_MODE.isEmpty())
        assertFalse(Constants.Preferences.KEY_APP_PACKAGE_A.isEmpty())
        assertFalse(Constants.Preferences.KEY_APP_PACKAGE_B.isEmpty())
    }

    @Test
    fun timeoutsArePositive() {
        assertTrue(Constants.Timeout.DEFAULT_MS > 0L)
        assertTrue(Constants.Timeout.SHORT_MS > 0L)
        assertTrue(Constants.Timeout.DEFAULT_MS > Constants.Timeout.SHORT_MS)
    }

    @Test
    fun freeTierLimitsMatchSplitScreenContext() {
        assertEquals(2, Constants.FreeTier.MAX_APPS)
        assertTrue(Constants.FreeTier.SESSION_MINUTES > 0)
    }
}

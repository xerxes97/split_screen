package com.example.split_screen.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LaunchPolicyTest {

    private val top = AppInfo(name = "Chrome", packageName = "com.android.chrome")
    private val bottom = AppInfo(name = "WhatsApp", packageName = "com.whatsapp")

    @Test
    fun bothAppsAllowLaunch() {
        assertTrue(LaunchPolicy.canLaunch(SplitSelection(top = top, bottom = bottom)))
    }

    @Test
    fun missingTopBlocksLaunch() {
        assertFalse(LaunchPolicy.canLaunch(SplitSelection(bottom = bottom)))
    }

    @Test
    fun missingBottomBlocksLaunch() {
        assertFalse(LaunchPolicy.canLaunch(SplitSelection(top = top)))
    }

    @Test
    fun emptySelectionBlocksLaunch() {
        assertFalse(LaunchPolicy.canLaunch(SplitSelection()))
    }
}

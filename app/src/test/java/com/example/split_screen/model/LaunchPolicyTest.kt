package com.example.split_screen.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LaunchPolicyTest {

    private val top = AppInfo(name = "Chrome", packageName = "com.android.chrome")
    private val bottom = AppInfo(name = "WhatsApp", packageName = "com.whatsapp")
    private val full = SplitSelection(top = top, bottom = bottom)

    @Test
    fun bothAppsAndServiceActiveAllowsLaunch() {
        assertTrue(LaunchPolicy.canLaunch(full, serviceActive = true))
    }

    @Test
    fun serviceInactiveBlocksLaunch() {
        assertFalse(LaunchPolicy.canLaunch(full, serviceActive = false))
    }

    @Test
    fun missingTopBlocksLaunch() {
        assertFalse(LaunchPolicy.canLaunch(SplitSelection(bottom = bottom), serviceActive = true))
    }

    @Test
    fun missingBottomBlocksLaunch() {
        assertFalse(LaunchPolicy.canLaunch(SplitSelection(top = top), serviceActive = true))
    }

    @Test
    fun emptySelectionBlocksLaunch() {
        assertFalse(LaunchPolicy.canLaunch(SplitSelection(), serviceActive = true))
    }
}

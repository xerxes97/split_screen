package com.example.split_screen.ui.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StatusBannerTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun statusBannerShowsActiveState() {
        composeRule.setContent {
            StatusBanner(isActive = true)
        }
        composeRule.onNodeWithText("Permiso de accesibilidad: ACTIVO")
            .assertIsDisplayed()
    }

    @Test
    fun statusBannerShowsInactiveState() {
        composeRule.setContent {
            StatusBanner(isActive = false)
        }
        composeRule.onNodeWithText("Permiso de accesibilidad: INACTIVO")
            .assertIsDisplayed()
    }
}

package com.example.split_screen.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.split_screen.R
import com.example.split_screen.model.AppInfo
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppSelectionCardTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun showsPlaceholderWhenNoAppSelected() {
        composeRule.setContent {
            AppSelectionCard(title = "App Superior", app = null, onClick = {})
        }
        composeRule.onNodeWithText("App Superior").assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.selection_none)).assertIsDisplayed()
    }

    @Test
    fun showsSelectedAppName() {
        composeRule.setContent {
            AppSelectionCard(
                title = "App Inferior",
                app = AppInfo("Chrome", "com.android.chrome"),
                onClick = {}
            )
        }
        composeRule.onNodeWithText("Chrome").assertIsDisplayed()
    }

    @Test
    fun clickInvokesCallback() {
        var clicks = 0
        composeRule.setContent {
            AppSelectionCard(title = "App Superior", app = null, onClick = { clicks++ })
        }
        composeRule.onNodeWithText(context.getString(R.string.selection_choose)).performClick()
        assertEquals(1, clicks)
    }

    @Test
    fun disabledButtonDoesNotInvokeCallback() {
        var clicks = 0
        composeRule.setContent {
            AppSelectionCard(title = "App Superior", app = null, onClick = { clicks++ }, enabled = false)
        }
        composeRule.onNodeWithText(context.getString(R.string.selection_choose))
            .assertIsNotEnabled()
            .performClick()
        assertEquals(0, clicks)
    }
}

package com.example.split_screen.ui.components

import android.content.Context
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.split_screen.R
import com.example.split_screen.billing.ProUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProCardTest {

    @get:Rule
    val compose = createComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()
    private var buys = 0

    private fun show(state: ProUiState) = compose.setContent {
        ProCard(state = state, onBuy = { buys++ })
    }

    @Test
    fun freeShowsPriceAndBuyWorks() {
        show(ProUiState.Free("US$ 2.99"))
        compose.onNodeWithText(context.getString(R.string.pro_buy, "US$ 2.99")).performClick()
        assertEquals(1, buys)
    }

    @Test
    fun unavailableDisablesBuy() {
        show(ProUiState.Unavailable)
        compose.onNodeWithText(context.getString(R.string.pro_unavailable)).assertIsNotEnabled()
    }

    @Test
    fun proShowsIndicatorWithoutBuy() {
        show(ProUiState.Pro)
        compose.onNodeWithText(context.getString(R.string.pro_active)).assertExists()
        compose.onNodeWithText(context.getString(R.string.pro_unavailable)).assertDoesNotExist()
    }
}

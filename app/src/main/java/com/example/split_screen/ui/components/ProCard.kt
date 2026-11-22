package com.example.split_screen.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.split_screen.R
import com.example.split_screen.billing.ProUiState
import com.example.split_screen.ui.theme.ProCrownInactive
import com.example.split_screen.ui.theme.ProGold
import com.example.split_screen.ui.theme.Split_screenTheme

/**
 * Pro area: gold crown in the top-right corner when the user owns Pro,
 * otherwise the "remove ads" purchase button with a gray crown.
 *
 * Only renders [state] and emits [onBuy]; knows nothing about Billing.
 */
@Composable
fun ProCard(
    state: ProUiState,
    onBuy: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state) {
        ProUiState.Pro -> Box(modifier = modifier.fillMaxWidth()) {
            Icon(
                painter = painterResource(R.drawable.ic_crown),
                contentDescription = stringResource(R.string.pro_crown_description),
                tint = ProGold,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(32.dp),
            )
        }
        is ProUiState.Free -> RemoveAdsButton(
            text = stringResource(R.string.pro_buy, state.formattedPrice),
            enabled = true,
            onClick = onBuy,
            modifier = modifier,
        )
        ProUiState.Unavailable -> RemoveAdsButton(
            text = stringResource(R.string.pro_unavailable),
            enabled = false,
            onClick = {},
            modifier = modifier,
        )
    }
}

@Composable
private fun RemoveAdsButton(text: String, enabled: Boolean, onClick: () -> Unit, modifier: Modifier) {
    Button(onClick = onClick, enabled = enabled, modifier = modifier.fillMaxWidth()) {
        Icon(
            painter = painterResource(R.drawable.ic_crown),
            contentDescription = null,
            tint = ProCrownInactive,
            modifier = Modifier.size(20.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text)
    }
}

@Preview(showBackground = true, name = "Gratis")
@Composable
private fun ProCardFreePreview() {
    Split_screenTheme { ProCard(state = ProUiState.Free("US$ 2.99"), onBuy = {}) }
}

@Preview(showBackground = true, name = "Pro")
@Composable
private fun ProCardProPreview() {
    Split_screenTheme { ProCard(state = ProUiState.Pro, onBuy = {}) }
}

@Preview(showBackground = true, name = "No disponible")
@Composable
private fun ProCardUnavailablePreview() {
    Split_screenTheme { ProCard(state = ProUiState.Unavailable, onBuy = {}) }
}

package com.example.split_screen.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.split_screen.R
import com.example.split_screen.billing.ProUiState
import com.example.split_screen.ui.theme.Split_screenTheme

/**
 * Tarjeta Pro: botón de compra o indicador de Pro activo.
 *
 * Solo muestra [state] y emite [onBuy]; no conoce Billing. La compra previa
 * se recupera sola al sincronizar con Google Play en cada inicio.
 */
@Composable
fun ProCard(
    state: ProUiState,
    onBuy: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            when (state) {
                ProUiState.Pro -> Text(
                    text = stringResource(R.string.pro_active),
                    style = MaterialTheme.typography.titleMedium,
                )
                is ProUiState.Free -> Button(onClick = onBuy, modifier = Modifier.fillMaxWidth()) {
                    Text(text = stringResource(R.string.pro_buy, state.formattedPrice))
                }
                ProUiState.Unavailable -> Button(onClick = {}, enabled = false, modifier = Modifier.fillMaxWidth()) {
                    Text(text = stringResource(R.string.pro_unavailable))
                }
            }
        }
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

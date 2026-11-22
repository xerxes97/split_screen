package com.example.split_screen.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.split_screen.R
import com.example.split_screen.billing.ProUiState

/**
 * Tarjeta Pro: compra, restauración o indicador de Pro activo.
 *
 * Solo muestra [state] y emite eventos; no conoce Billing.
 */
@Composable
fun ProCard(
    state: ProUiState,
    onBuy: () -> Unit,
    onRestore: () -> Unit,
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
                is ProUiState.Free -> {
                    Button(onClick = onBuy, modifier = Modifier.fillMaxWidth()) {
                        Text(text = stringResource(R.string.pro_buy, state.formattedPrice))
                    }
                    RestoreButton(onRestore)
                }
                ProUiState.Unavailable -> {
                    Button(onClick = {}, enabled = false, modifier = Modifier.fillMaxWidth()) {
                        Text(text = stringResource(R.string.pro_unavailable))
                    }
                    RestoreButton(onRestore)
                }
            }
        }
    }
}

@Composable
private fun RestoreButton(onRestore: () -> Unit) {
    TextButton(onClick = onRestore, modifier = Modifier.fillMaxWidth()) {
        Text(text = stringResource(R.string.pro_restore))
    }
}

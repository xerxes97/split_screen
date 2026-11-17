package com.example.split_screen.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.split_screen.R

/** Botón que inicia la pantalla dividida con las apps elegidas. */
@Composable
fun LaunchButton(enabled: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(onClick = onClick, enabled = enabled, modifier = modifier.fillMaxWidth()) {
        Text(text = stringResource(R.string.launch_button))
    }
}

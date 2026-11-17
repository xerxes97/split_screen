package com.example.split_screen.ui.components

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.split_screen.R

@Composable
fun StatusBanner(isActive: Boolean, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val color = if (isActive) {
        Color(ContextCompat.getColor(context, R.color.banner_status_active_color))
    } else {
        Color(ContextCompat.getColor(context, R.color.banner_status_inactive_color))
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .wrapContentWidth(Alignment.CenterHorizontally),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = if (isActive) stringResource(R.string.banner_status_active)
            else stringResource(R.string.banner_status_inactive),
            color = if (isActive) Color(ContextCompat.getColor(LocalContext.current, R.color.banner_status_active_color))
            else Color(ContextCompat.getColor(LocalContext.current, R.color.banner_status_inactive_color)),
            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
        )

        if (!isActive) {
            val activityContext = LocalContext.current
            Button(onClick = {
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                activityContext.startActivity(intent)
            }) {
                Text(
                    text = stringResource(R.string.banner_settings_button),
                    fontSize = 14.sp
                )
            }
        }
    }
}
package com.example.split_screen.ui.components

import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
    val text = if (isActive) {
        context.getString(R.string.banner_status_active)
    } else {
        context.getString(R.string.banner_status_inactive)
    }

    Text(
        text = text,
        color = color,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .wrapContentWidth(Alignment.CenterHorizontally),
        style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
    )
}

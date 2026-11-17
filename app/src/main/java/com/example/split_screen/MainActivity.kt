package com.example.split_screen

import android.os.Bundle
import android.provider.Settings
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.split_screen.Constants
import com.example.split_screen.ui.components.StatusBanner
import com.example.split_screen.util.AccessibilityHelper
import com.example.split_screen.ui.theme.Split_screenTheme
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Split_screenTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainContent(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // El estado se actualiza automáticamente vía recomposición al volver de Settings
    }
}

@Composable
fun MainContent(modifier: Modifier = Modifier) {
    var isActive by remember { mutableStateOf(false) }
    
    // Actualizar estado al componer y en onResume
    val context = LocalContext.current
    val isServiceActive = AccessibilityHelper.isServiceActive(context)
    if (isActive != isServiceActive) {
        isActive = isServiceActive
    }

    StatusBanner(
        isActive = isActive,
        modifier = Modifier.padding(16.dp)
    )
    
    Greeting(
        name = Constants.Greeting.DEFAULT_NAME,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    androidx.compose.material3.Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Split_screenTheme {
        Greeting("Android")
    }
}
package com.example.split_screen

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.split_screen.model.AppInfo
import com.example.split_screen.model.SplitSelection
import com.example.split_screen.provider.InstalledAppsProvider
import com.example.split_screen.ui.components.AppPickerDialog
import com.example.split_screen.ui.components.AppSelectionCard
import com.example.split_screen.ui.components.StatusBanner
import com.example.split_screen.ui.theme.Split_screenTheme
import com.example.split_screen.util.AccessibilityHelper
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var selection by mutableStateOf(SplitSelection())
    private var installedApps: List<AppInfo> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        lifecycleScope.launch {
            installedApps = InstalledAppsProvider(applicationContext).getInstalledApps()
        }
        setContent {
            Split_screenTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainContent(
                        selection = selection,
                        onPickTop = ::showTopPicker,
                        onPickBottom = ::showBottomPicker,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    private fun showTopPicker() {
        showPicker(selection.availableForTop(installedApps), Constants.Picker.TAG_TOP) { app ->
            selection = selection.selectTop(app)
        }
    }

    private fun showBottomPicker() {
        showPicker(selection.availableForBottom(installedApps), Constants.Picker.TAG_BOTTOM) { app ->
            selection = selection.selectBottom(app)
        }
    }

    private fun showPicker(apps: List<AppInfo>, tag: String, onSelected: (AppInfo) -> Unit) {
        if (supportFragmentManager.findFragmentByTag(tag) != null) return
        val dialog = AppPickerDialog.newInstance(apps)
        dialog.setOnAppClickListener(onSelected)
        dialog.show(supportFragmentManager, tag)
    }
}

@Composable
fun MainContent(
    selection: SplitSelection,
    onPickTop: () -> Unit,
    onPickBottom: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isServiceActive = AccessibilityHelper.isServiceActive(LocalContext.current)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatusBanner(isActive = isServiceActive, modifier = Modifier.fillMaxWidth())
        AppSelectionCard(
            title = stringResource(R.string.selection_top_title),
            app = selection.top,
            onClick = onPickTop
        )
        AppSelectionCard(
            title = stringResource(R.string.selection_bottom_title),
            app = selection.bottom,
            onClick = onPickBottom
        )
    }
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

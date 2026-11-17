package com.example.split_screen

import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.os.BundleCompat
import androidx.lifecycle.lifecycleScope
import com.example.split_screen.model.AppInfo
import com.example.split_screen.model.LaunchPolicy
import com.example.split_screen.model.SplitSelection
import com.example.split_screen.provider.InstalledAppsProvider
import com.example.split_screen.service.SplitAccessibilityService.LaunchResult
import com.example.split_screen.service.SplitServiceHolder
import com.example.split_screen.ui.messageRes
import com.example.split_screen.ui.components.AppPickerDialog
import com.example.split_screen.ui.components.AppSelectionCard
import com.example.split_screen.ui.components.LaunchButton
import com.example.split_screen.ui.components.StatusBanner
import com.example.split_screen.ui.theme.Split_screenTheme
import com.example.split_screen.util.AccessibilityHelper
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var selection by mutableStateOf(SplitSelection())
    private var installedApps by mutableStateOf<List<AppInfo>?>(null)
    private var isServiceActive by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        selection = SplitSelection.readFrom(savedInstanceState)
        registerPickerResult(Constants.Picker.TAG_TOP) { selection = selection.selectTop(it) }
        registerPickerResult(Constants.Picker.TAG_BOTTOM) { selection = selection.selectBottom(it) }
        lifecycleScope.launch {
            installedApps = InstalledAppsProvider(applicationContext).getInstalledApps()
        }
        setContent {
            Split_screenTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainContent(
                        selection = selection,
                        isServiceActive = isServiceActive,
                        appsLoaded = installedApps != null,
                        onPickTop = {
                            showPicker(selection.availableForTop(installedApps.orEmpty()), Constants.Picker.TAG_TOP)
                        },
                        onPickBottom = {
                            showPicker(selection.availableForBottom(installedApps.orEmpty()), Constants.Picker.TAG_BOTTOM)
                        },
                        onLaunch = ::launchSplit,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    /** Re-consulta el servicio al volver (p. ej. desde Ajustes de accesibilidad). */
    override fun onResume() {
        super.onResume()
        isServiceActive = AccessibilityHelper.isServiceActive(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        selection.writeTo(outState)
    }

    /** Escucha el resultado del picker [tag]; se re-registra tras recreación. */
    private fun registerPickerResult(tag: String, onSelected: (AppInfo) -> Unit) {
        supportFragmentManager.setFragmentResultListener(tag, this) { _, result ->
            BundleCompat.getParcelable(result, Constants.Picker.RESULT_APP, AppInfo::class.java)
                ?.let(onSelected)
        }
    }

    private fun launchSplit() {
        val top = selection.top ?: return
        val bottom = selection.bottom ?: return
        val result = SplitServiceHolder.current()?.launchSplitScreen(top.packageName, bottom.packageName)
            ?: LaunchResult.Failure(LaunchResult.LaunchError.SERVICE_NOT_CONNECTED)
        if (result is LaunchResult.Failure) {
            Toast.makeText(this, result.error.messageRes(), Toast.LENGTH_LONG).show()
        }
    }

    private fun showPicker(apps: List<AppInfo>, tag: String) {
        if (supportFragmentManager.isStateSaved) return
        if (supportFragmentManager.findFragmentByTag(tag) != null) return
        AppPickerDialog.newInstance(apps).show(supportFragmentManager, tag)
    }
}

@Composable
fun MainContent(
    selection: SplitSelection,
    isServiceActive: Boolean,
    appsLoaded: Boolean,
    onPickTop: () -> Unit,
    onPickBottom: () -> Unit,
    onLaunch: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            onClick = onPickTop,
            enabled = appsLoaded
        )
        AppSelectionCard(
            title = stringResource(R.string.selection_bottom_title),
            app = selection.bottom,
            onClick = onPickBottom,
            enabled = appsLoaded
        )
        LaunchButton(
            enabled = LaunchPolicy.canLaunch(selection, isServiceActive),
            onClick = onLaunch
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

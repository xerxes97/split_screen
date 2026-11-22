package com.example.split_screen

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.example.split_screen.billing.PlayBillingGateway
import com.example.split_screen.billing.ProProduct
import com.example.split_screen.billing.ProStatusSyncer
import com.example.split_screen.billing.ProUiState
import com.example.split_screen.ui.components.ProCard
import com.example.split_screen.launcher.PlanResult
import com.example.split_screen.launcher.SplitLauncher
import com.example.split_screen.model.AppInfo
import com.example.split_screen.model.LaunchPolicy
import com.example.split_screen.model.SplitSelection
import com.example.split_screen.provider.InstalledAppsProvider
import com.example.split_screen.storage.ProStatusStorageManager
import com.example.split_screen.storage.proStatusDataStore
import com.example.split_screen.ui.messageRes
import com.example.split_screen.ui.components.AppPickerDialog
import com.example.split_screen.ui.components.AppSelectionCard
import com.example.split_screen.ui.components.LaunchButton
import com.example.split_screen.ui.theme.Split_screenTheme
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var selection by mutableStateOf(SplitSelection())
    private var installedApps by mutableStateOf<List<AppInfo>?>(null)
    private val splitLauncher by lazy { SplitLauncher(this) }
    private val billingGateway by lazy { PlayBillingGateway(this, onPurchasesUpdated = ::syncProStatus) }
    private val proStatusStorage by lazy { ProStatusStorageManager(applicationContext.proStatusDataStore) }
    private val proStatusSyncer by lazy { ProStatusSyncer(billingGateway, proStatusStorage) }
    private var isPro by mutableStateOf(false)
    private var proProduct by mutableStateOf<ProProduct?>(null)

    override fun onResume() {
        super.onResume()
        syncProStatus()
        if (proProduct == null) loadProProduct()
    }

    /** Google Play Billing is the source of truth; refresh the cache on every resume. */
    private fun syncProStatus() {
        lifecycleScope.launch { proStatusSyncer.sync() }
    }

    private fun loadProProduct() {
        lifecycleScope.launch { proProduct = billingGateway.queryProProduct(Constants.Billing.PRODUCT_ID_PRO) }
    }

    private fun buyPro() {
        if (!billingGateway.launchPurchase(this)) {
            Toast.makeText(this, R.string.pro_billing_error, Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        selection = SplitSelection.readFrom(savedInstanceState)
        registerPickerResult(Constants.Picker.TAG_TOP) { selection = selection.selectTop(it) }
        registerPickerResult(Constants.Picker.TAG_BOTTOM) { selection = selection.selectBottom(it) }
        lifecycleScope.launch {
            installedApps = InstalledAppsProvider(applicationContext).getInstalledApps()
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) { proStatusStorage.isProUser.collect { isPro = it } }
        }
        setContent {
            Split_screenTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainContent(
                        proState = ProUiState.from(isPro, proProduct),
                        onBuyPro = ::buyPro,
                        selection = selection,
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

    override fun onDestroy() {
        splitLauncher.cancel()
        billingGateway.close()
        super.onDestroy()
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
        val result = splitLauncher.launch(top.packageName, bottom.packageName)
        if (result is PlanResult.Failure) {
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
    proState: ProUiState,
    onBuyPro: () -> Unit,
    selection: SplitSelection,
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
        ProCard(state = proState, onBuy = onBuyPro)
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
            enabled = LaunchPolicy.canLaunch(selection),
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

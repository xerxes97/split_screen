package com.example.split_screen.ui.components

import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.split_screen.MainActivity
import com.example.split_screen.R
import com.example.split_screen.model.AppInfo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppPickerDialogTest {

    private val apps = listOf(
        AppInfo(name = "Chrome", packageName = "com.android.chrome"),
        AppInfo(name = "Calculadora", packageName = "com.android.calculator2"),
        AppInfo(name = "WhatsApp", packageName = "com.whatsapp")
    )

    private fun withShownDialog(block: (AppPickerDialog) -> Unit) {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val dialog = AppPickerDialog.newInstance(apps)
                dialog.show(activity.supportFragmentManager, TEST_TAG)
                activity.supportFragmentManager.executePendingTransactions()
                block(dialog)
            }
        }
    }

    @Test
    fun dialogShowsWithoutCrash() {
        withShownDialog { dialog ->
            val view = dialog.requireView()
            assertNotNull(view.findViewById<RecyclerView>(R.id.rvApps))
            assertNotNull(view.findViewById<EditText>(R.id.etSearch))
        }
    }

    @Test
    fun searchFiltersList() {
        withShownDialog { dialog ->
            val view = dialog.requireView()
            val rv = view.findViewById<RecyclerView>(R.id.rvApps)
            assertEquals(apps.size, rv.adapter?.itemCount)

            view.findViewById<EditText>(R.id.etSearch).setText("chr")
            // TextWatcher se dispara síncrono en el hilo principal.
            assertEquals(1, rv.adapter?.itemCount)
        }
    }

    private companion object {
        const val TEST_TAG = "picker_test"
    }
}

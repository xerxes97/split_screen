package com.example.split_screen.ui.components

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.split_screen.Constants
import com.example.split_screen.R
import com.example.split_screen.model.AppInfo
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AppPickerDialog : BottomSheetDialogFragment() {

    private var allApps: List<AppInfo> = emptyList()
    private lateinit var adapter: AppPickerAdapter

    companion object {
        fun newInstance(apps: List<AppInfo>): AppPickerDialog {
            val dialog = AppPickerDialog()
            val args = Bundle()
            args.putParcelableArrayList("apps", appInfoListToBundle(apps))
            dialog.arguments = args
            return dialog
        }

        private fun appInfoListToBundle(apps: List<AppInfo>): ArrayList<AppInfo> {
            val list = ArrayList<AppInfo>()
            list.addAll(apps)
            return list
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val list = it.getParcelableArrayList<AppInfo>("apps")
            if (list != null) allApps = list
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_app_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvApps = view.findViewById<RecyclerView>(R.id.rvApps)
        rvApps.layoutManager = LinearLayoutManager(requireContext())
        adapter = AppPickerAdapter(requireContext(), allApps) { appInfo ->
            // Resultado vía Fragment Result API: sobrevive a rotación/recreación.
            // requestKey = tag del diálogo, para distinguir picker superior/inferior.
            tag?.let { requestKey ->
                parentFragmentManager.setFragmentResult(requestKey, bundleOf(Constants.Picker.RESULT_APP to appInfo))
            }
            dismiss()
        }
        rvApps.adapter = adapter

        view.findViewById<EditText>(R.id.etSearch).addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                adapter.setFilter(s?.toString().orEmpty())
            }
        })

        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnCancel)
            .setOnClickListener { dismiss() }
    }
}
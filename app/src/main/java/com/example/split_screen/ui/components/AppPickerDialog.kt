package com.example.split_screen.ui.components

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.split_screen.R
import com.example.split_screen.model.AppInfo
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AppPickerDialog : BottomSheetDialogFragment() {

    private var allApps: List<AppInfo> = emptyList()
    private var onAppClickListener: ((AppInfo) -> Unit)? = null
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
            onAppClickListener?.invoke(appInfo)
            dismiss()
        }
        rvApps.adapter = adapter

        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnCancel)
            .setOnClickListener { dismiss() }
    }

    fun setOnAppClickListener(listener: (AppInfo) -> Unit) {
        onAppClickListener = listener
    }
}
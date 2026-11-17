package com.example.split_screen.ui.components

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.split_screen.R
import com.example.split_screen.model.AppInfo

class AppPickerAdapter(
    private val context: Context,
    private val apps: List<AppInfo>,
    private val onAppClick: (AppInfo) -> Unit
) : RecyclerView.Adapter<AppPickerAdapter.AppViewHolder>() {

    private val pm: PackageManager = context.packageManager

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_app_picker, parent, false)
        return AppViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val app = apps[position]
        holder.bind(app)
    }

    override fun getItemCount(): Int = apps.size

    inner class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivIcon: ImageView = itemView.findViewById(R.id.ivAppIcon)
        private val tvName: TextView = itemView.findViewById(R.id.tvAppName)

        fun bind(app: AppInfo) {
            tvName.text = app.name
            
            // Carga lazy del ícono solo para la fila visible
            try {
                val icon = pm.getApplicationIcon(app.packageName)
                ivIcon.setImageDrawable(icon)
            } catch (e: Exception) {
                ivIcon.setImageResource(R.drawable.ic_launcher_foreground)
            }

            itemView.setOnClickListener { onAppClick(app) }
        }

        fun clearIcon() {
            ivIcon.setImageDrawable(null)
        }
    }

    override fun onViewRecycled(holder: AppViewHolder) {
        super.onViewRecycled(holder)
        holder.clearIcon()
    }
}
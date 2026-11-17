package com.example.split_screen.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Modelo inmutable de información de una aplicación instalada.
 *
 * No guarda íconos (Drawable/Bitmap) para evitar consumo de memoria.
 * El ícono se carga bajo demanda vía [PackageManager.getApplicationIcon].
 */
data class AppInfo(
    val name: String,
    val packageName: String
) : Parcelable {
    override fun toString(): String = "AppInfo(name='$name', packageName='$packageName')"

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(packageName)
    }

    override fun describeContents(): Int = 0

    companion object {
        @Suppress("UNUSED_PARAMETER")
        @JvmField
        val CREATOR: Parcelable.Creator<AppInfo> = object : Parcelable.Creator<AppInfo> {
            override fun createFromParcel(parcel: Parcel): AppInfo {
                return AppInfo(
                    parcel.readString() ?: "",
                    parcel.readString() ?: ""
                )
            }

            override fun newArray(size: Int): Array<AppInfo?> {
                return arrayOfNulls(size)
            }
        }
    }
}
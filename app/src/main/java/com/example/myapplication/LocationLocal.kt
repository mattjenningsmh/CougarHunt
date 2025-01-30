package com.example.cougarhunt
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocationLocal(
    val address: String,
    val description: String?,
    val imageLink: Int,
    val hint: String?,
    val latLng: LatLng,
    val code: String,
): Parcelable
package me.xianglun.blinkblinkbeachadmin.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat

@Parcelize
data class Event(
    val eventID: String = "",
    val longitude: Double = 0.0,
    val latitude: Double = 0.0,
    val address: String = "",
    val name: String = "",
    val dateOfCreation: String = DateFormat.getDateTimeInstance()
        .format(System.currentTimeMillis()),
    val startTimeMillis: Long = 0,
    val endTimeMillis: Long = 0,
    val imageUrl: String = "",
    val detail: String = "",
    val participantIDs: List<String> = mutableListOf(),
) : Parcelable

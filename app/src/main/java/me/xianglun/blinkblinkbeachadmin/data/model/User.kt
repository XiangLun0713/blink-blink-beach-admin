package me.xianglun.blinkblinkbeachadmin.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val id: String = "",
    val username: String = "",
    val email: String = "",
    val profileImageUrl: String = "",
    val eventsParticipated: List<String> = listOf()
) : Parcelable

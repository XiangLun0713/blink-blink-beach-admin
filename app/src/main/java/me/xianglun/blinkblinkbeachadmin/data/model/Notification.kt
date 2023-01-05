package me.xianglun.blinkblinkbeachadmin.data.model

data class Notification(
    val id: String = "",
    val description: String = "",
    val dateInMillis: Long = System.currentTimeMillis(),
)

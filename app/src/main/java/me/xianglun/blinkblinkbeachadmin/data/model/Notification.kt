package me.xianglun.blinkblinkbeachadmin.data.model

data class Notification(
    val id: String = "",
    var description: String = "",
    val dateInMillis: Long = System.currentTimeMillis(),
)

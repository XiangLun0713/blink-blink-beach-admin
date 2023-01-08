package me.xianglun.blinkblinkbeachadmin.data.repository.createEvent

import android.net.Uri
import me.xianglun.blinkblinkbeachadmin.data.model.Event
import me.xianglun.blinkblinkbeachadmin.util.APIStateWithValue

interface CreateEventRepository {
    suspend fun uploadEventImageToStorage(uri: Uri): APIStateWithValue<String>
    suspend fun createEventAndSendNotificationOnFirestore(
        event: Event,
        reportId: String
    ): APIStateWithValue<Event>
}
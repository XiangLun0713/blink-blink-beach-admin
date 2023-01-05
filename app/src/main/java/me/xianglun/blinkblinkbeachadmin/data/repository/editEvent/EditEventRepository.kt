package me.xianglun.blinkblinkbeachadmin.data.repository.editEvent

import android.net.Uri
import me.xianglun.blinkblinkbeachadmin.data.model.Event
import me.xianglun.blinkblinkbeachadmin.util.APIStateWithValue

interface EditEventRepository {
    suspend fun uploadEventImageToStorage(uri: Uri): APIStateWithValue<String>
    suspend fun saveEventOnFirestore(event: Event): APIStateWithValue<Event>
}
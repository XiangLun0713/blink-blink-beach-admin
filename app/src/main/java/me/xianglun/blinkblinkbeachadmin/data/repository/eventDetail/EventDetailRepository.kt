package me.xianglun.blinkblinkbeachadmin.data.repository.eventDetail

import me.xianglun.blinkblinkbeachadmin.data.model.Event
import me.xianglun.blinkblinkbeachadmin.util.APIState

interface EventDetailRepository {
    suspend fun editEvent(modifiedEvent: Event, eventID: String): APIState
    suspend fun deleteEvent(eventID: String): APIState
}
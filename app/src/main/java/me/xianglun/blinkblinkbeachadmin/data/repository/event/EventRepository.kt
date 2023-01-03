package me.xianglun.blinkblinkbeachadmin.data.repository.event

import kotlinx.coroutines.flow.Flow
import me.xianglun.blinkblinkbeachadmin.data.model.Event
import me.xianglun.blinkblinkbeachadmin.data.model.Response

typealias Events = List<Event>
typealias EventsResponse = Response<Events>

interface EventRepository {
    fun getAllEventsFromFirestore(): Flow<EventsResponse>
}
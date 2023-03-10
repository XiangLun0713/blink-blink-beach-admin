package me.xianglun.blinkblinkbeachadmin.data.repository.event

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import me.xianglun.blinkblinkbeachadmin.data.model.Event
import me.xianglun.blinkblinkbeachadmin.data.model.Response
import me.xianglun.blinkblinkbeachadmin.util.APIState
import me.xianglun.blinkblinkbeachadmin.util.Constants
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepositoryImpl @Inject constructor(
    firestore: FirebaseFirestore,
) : EventRepository {

    private val eventsCollection = firestore.collection(Constants.EVENTS)
    private val triggerCollection = firestore.collection(Constants.TRIGGER)

    override fun getAllEventsFromFirestore() = callbackFlow {
        val snapshotListener =
            eventsCollection.addSnapshotListener { snapshot, e ->
                val eventsResponse = if (snapshot != null) {
                    val eventList = mutableListOf<Event>()
                    for (eventSnapshot in snapshot) {
                        val event = eventSnapshot.toObject(Event::class.java)
                        event.eventID = eventSnapshot.id
                        eventList.add(event)
                    }
                    Response.Success(eventList.sortedBy { it.startTimeMillis })
                } else {
                    Response.Failure(e)
                }
                trySend(eventsResponse)
            }

        awaitClose {
            snapshotListener.remove()
        }
    }

    override suspend fun sendCertificate(): APIState {
        return try {
            triggerCollection.add(mapOf<Any, Any>()).await()
            APIState.Success
        } catch (e: Exception) {
            APIState.Error(e.message)
        }
    }
}
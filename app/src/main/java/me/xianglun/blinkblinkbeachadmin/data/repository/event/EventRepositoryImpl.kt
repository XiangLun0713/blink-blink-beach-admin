package me.xianglun.blinkblinkbeachadmin.data.repository.event

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import me.xianglun.blinkblinkbeachadmin.data.model.Event
import me.xianglun.blinkblinkbeachadmin.data.model.Response
import me.xianglun.blinkblinkbeachadmin.util.Constants
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepositoryImpl @Inject constructor(
    firestore: FirebaseFirestore,
) : EventRepository {

    private val eventsCollection = firestore.collection(Constants.EVENTS)

    override fun getAllEventsFromFirestore() = callbackFlow {
        val snapshotListener =
            eventsCollection.addSnapshotListener { snapshot, e ->
                val eventsResponse = if (snapshot != null) {
                    val events = snapshot.toObjects(Event::class.java)
                    Response.Success(events)
                } else {
                    Response.Failure(e)
                }
                trySend(eventsResponse)
            }

        awaitClose {
            snapshotListener.remove()
        }
    }
}
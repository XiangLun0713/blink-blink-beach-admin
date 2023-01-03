package me.xianglun.blinkblinkbeachadmin.data.repository.eventDetail

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import me.xianglun.blinkblinkbeachadmin.data.model.Event
import me.xianglun.blinkblinkbeachadmin.util.APIState
import me.xianglun.blinkblinkbeachadmin.util.Constants
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventDetailRepositoryImpl @Inject constructor(
    firestore: FirebaseFirestore,
) : EventDetailRepository {

    private val eventsCollection = firestore.collection(Constants.EVENTS)

    override suspend fun editEvent(modifiedEvent: Event, eventID: String): APIState {
        return try {
            eventsCollection.document(eventID).set(modifiedEvent).await()
            APIState.Success
        } catch (e: Exception) {
            APIState.Error(e.message)
        }
    }

    override suspend fun deleteEvent(eventID: String): APIState {
        return try {
            eventsCollection.document(eventID).delete().await()
            // todo rethink whether we need to remove the event from user's registered event list
            APIState.Success
        } catch (e: Exception) {
            APIState.Error(e.message)
        }
    }
}
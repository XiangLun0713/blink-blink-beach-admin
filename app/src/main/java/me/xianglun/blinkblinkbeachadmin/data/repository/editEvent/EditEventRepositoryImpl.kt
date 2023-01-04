package me.xianglun.blinkblinkbeachadmin.data.repository.editEvent

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import me.xianglun.blinkblinkbeachadmin.data.model.Event
import me.xianglun.blinkblinkbeachadmin.util.APIStateWithValue
import me.xianglun.blinkblinkbeachadmin.util.Constants
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EditEventRepositoryImpl @Inject constructor(
    private val storageReference: StorageReference,
    private val firestore: FirebaseFirestore,
) : EditEventRepository {

    override suspend fun uploadEventImageToStorage(uri: Uri): APIStateWithValue<String> {
        return try {
            // store image to firebase storage
            val childRef = storageReference.child("event/${uri.lastPathSegment}")
            val uploadTask = childRef.putFile(uri).await()
            val imageUrl = uploadTask.metadata?.reference?.downloadUrl?.await() ?: throw Exception()
            APIStateWithValue.Success(imageUrl.toString())
        } catch (e: Exception) {
            // return error state
            APIStateWithValue.Error(e.message)
        }
    }

    override suspend fun saveEventOnFirestore(event: Event): APIStateWithValue<Event> {
        return try {
            // update event on firestore
            firestore.collection(Constants.EVENTS).document(event.eventID).set(event).await()
            APIStateWithValue.Success(event)
        } catch (e: Exception) {
            APIStateWithValue.Error(e.message)
        }
    }

}
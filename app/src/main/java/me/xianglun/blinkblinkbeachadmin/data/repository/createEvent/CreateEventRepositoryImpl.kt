package me.xianglun.blinkblinkbeachadmin.data.repository.createEvent

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import me.xianglun.blinkblinkbeachadmin.data.model.Event
import me.xianglun.blinkblinkbeachadmin.data.model.Notification
import me.xianglun.blinkblinkbeachadmin.util.APIStateWithValue
import me.xianglun.blinkblinkbeachadmin.util.ReportStatus
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreateEventRepositoryImpl @Inject constructor(
    private val storageReference: StorageReference,
    private val firestore: FirebaseFirestore,
) : CreateEventRepository {
    override suspend fun uploadEventImageToStorage(uri: Uri): APIStateWithValue<String> {
        return try {
            val childRef = storageReference.child("event/${uri.lastPathSegment}")
            val uploadTask = childRef.putFile(uri).await()
            val imageUrl = uploadTask.metadata?.reference?.downloadUrl?.await() ?: throw Exception()
            APIStateWithValue.Success(imageUrl.toString())
        } catch (e: Exception) {
            APIStateWithValue.Error(e.message)
        }
    }

    override suspend fun createEventAndSendNotificationOnFirestore(
        event: Event,
        reportId: String
    ): APIStateWithValue<Event> {
        return try {
            firestore.collection("events").add(event).await()
            firestore.collection("reports").document(reportId)
                .update("status", ReportStatus.APPROVED).await()
            val notification = Notification()
            notification.description =
                "Hey! There is a new cleanup event in your area, ${event.name}."
            firestore.collection("notifications").add(notification).await()
            APIStateWithValue.Success(event)
        } catch (e: Exception) {
            APIStateWithValue.Error(e.message)
        }
    }

}
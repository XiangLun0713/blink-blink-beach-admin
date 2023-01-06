package me.xianglun.blinkblinkbeachadmin.data.repository.userDetail

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import me.xianglun.blinkblinkbeachadmin.data.model.User
import me.xianglun.blinkblinkbeachadmin.util.APIState
import me.xianglun.blinkblinkbeachadmin.util.APIStateWithValue
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDetailRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storageReference: StorageReference,
    private val firebaseStorage: FirebaseStorage
) : UserDetailRepository {

    private val usersCollection = firestore.collection("users")

    override suspend fun saveUserProfile(
        userId: String,
        fileUri: Uri?,
        username: String
    ): APIStateWithValue<User> {
        var user = User()
        return try {
            val userDocument = usersCollection.document(userId)

            if (fileUri != null) {
                val childRef = storageReference.child("report/${fileUri.lastPathSegment}")
                val uploadTask = childRef.putFile(fileUri).await()
                val imageUrl =
                    uploadTask.metadata?.reference?.downloadUrl?.await() ?: throw Exception()
                val oldProfileImageUrl =
                    userDocument.get().await().toObject(User::class.java)?.profileImageUrl
                if (oldProfileImageUrl != null) {
                    if (oldProfileImageUrl.isNotEmpty()) {
                        try {
                            firebaseStorage.getReferenceFromUrl(oldProfileImageUrl).delete().await()
                        } catch (e: StorageException) {
                            Log.d("FUCK", "$e")
                        }
                    }
                }
                userDocument.update("profileImageUrl", imageUrl).await()
            }

            userDocument.update("username", username).await()
            val userSnapshot = userDocument.get().await().toObject(User::class.java)
            if (userSnapshot != null) {
                user = userSnapshot
            }
            APIStateWithValue.Success(user)
        } catch (e: Exception) {
            APIStateWithValue.Error(e.message)
        }
    }

    override suspend fun deleteUser(userId: String): APIState {
        return try {
            usersCollection.document(userId).delete().await()
            APIState.Success
        } catch (e: Exception) {
            APIState.Error(e.message)
        }
    }
}
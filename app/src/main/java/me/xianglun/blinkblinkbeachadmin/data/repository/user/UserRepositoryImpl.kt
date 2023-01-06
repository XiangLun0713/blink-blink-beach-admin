package me.xianglun.blinkblinkbeachadmin.data.repository.user

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import me.xianglun.blinkblinkbeachadmin.data.model.User
import me.xianglun.blinkblinkbeachadmin.util.APIStateWithValue
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
) : UserRepository {

    private val userCollection = firestore.collection("users")

    override suspend fun fetchUserList(): APIStateWithValue<List<User>> {
        val userList = mutableListOf<User>()

        return try {
            val userListSnapshot = userCollection.get().await()
            for (userSnapshot in userListSnapshot) {
                val user = userSnapshot.toObject(User::class.java)
                user.id = userSnapshot.id
                userList.add(user)
            }
            APIStateWithValue.Success(userList)
        } catch (e: Exception) {
            APIStateWithValue.Error(e.message)
        }
    }
}
package me.xianglun.blinkblinkbeachadmin.data.repository.userDetail

import android.net.Uri
import me.xianglun.blinkblinkbeachadmin.data.model.User
import me.xianglun.blinkblinkbeachadmin.util.APIState
import me.xianglun.blinkblinkbeachadmin.util.APIStateWithValue

interface UserDetailRepository {
    suspend fun saveUserProfile(
        userId: String,
        fileUri: Uri?,
        username: String
    ): APIStateWithValue<User>

    suspend fun deleteUser(userId: String): APIState
}
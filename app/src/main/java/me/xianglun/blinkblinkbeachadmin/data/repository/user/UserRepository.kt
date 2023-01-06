package me.xianglun.blinkblinkbeachadmin.data.repository.user

import me.xianglun.blinkblinkbeachadmin.data.model.User
import me.xianglun.blinkblinkbeachadmin.util.APIStateWithValue

interface UserRepository {
    suspend fun fetchUserList(): APIStateWithValue<List<User>>
}
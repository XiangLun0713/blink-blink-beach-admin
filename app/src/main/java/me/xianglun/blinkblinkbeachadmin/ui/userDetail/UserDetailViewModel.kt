package me.xianglun.blinkblinkbeachadmin.ui.userDetail

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import me.xianglun.blinkblinkbeachadmin.data.model.User
import me.xianglun.blinkblinkbeachadmin.data.repository.userDetail.UserDetailRepository
import me.xianglun.blinkblinkbeachadmin.util.APIState
import me.xianglun.blinkblinkbeachadmin.util.APIStateWithValue
import javax.inject.Inject

@HiltViewModel
class UserDetailViewModel @Inject constructor(
    private val repository: UserDetailRepository
) : ViewModel() {
    private val _user = MutableLiveData<APIStateWithValue<User>>()
    val user: LiveData<APIStateWithValue<User>> = _user
    private val apiStateChannel = Channel<APIState>()
    val apiState = apiStateChannel.receiveAsFlow()

    fun saveUserProfile(userId: String, fileUri: Uri?, username: String) = viewModelScope.launch {
        _user.value = repository.saveUserProfile(userId, fileUri, username)
    }

    fun deleteUser(userId: String) = viewModelScope.launch {
        apiStateChannel.send(APIState.Loading)
        apiStateChannel.send(repository.deleteUser(userId))
    }
}
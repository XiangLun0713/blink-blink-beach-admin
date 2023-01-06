package me.xianglun.blinkblinkbeachadmin.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import me.xianglun.blinkblinkbeachadmin.data.model.User
import me.xianglun.blinkblinkbeachadmin.data.repository.user.UserRepository
import me.xianglun.blinkblinkbeachadmin.util.APIStateWithValue
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {
    private val _userList = MutableLiveData<APIStateWithValue<List<User>>>()
    val userList: LiveData<APIStateWithValue<List<User>>> = _userList

    fun populateUserList() = viewModelScope.launch {
        _userList.value = repository.fetchUserList()
    }

    fun clearCurrentUserList() = viewModelScope.launch {
        _userList.value = APIStateWithValue.Success(emptyList())
    }
}
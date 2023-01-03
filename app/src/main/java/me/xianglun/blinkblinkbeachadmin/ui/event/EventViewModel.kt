package me.xianglun.blinkblinkbeachadmin.ui.event

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import me.xianglun.blinkblinkbeachadmin.data.model.Response
import me.xianglun.blinkblinkbeachadmin.data.repository.event.EventRepository
import me.xianglun.blinkblinkbeachadmin.data.repository.event.EventsResponse
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val repository: EventRepository
): ViewModel() {

    private val _eventsResponse = MutableLiveData<EventsResponse>(Response.Loading)
    val eventsResponse: LiveData<EventsResponse> = _eventsResponse

    init {
        getEvents()
    }

    private fun getEvents() = viewModelScope.launch {
        repository.getEventsFromFirestore().collect { response ->
            _eventsResponse.value = response
        }
    }

}
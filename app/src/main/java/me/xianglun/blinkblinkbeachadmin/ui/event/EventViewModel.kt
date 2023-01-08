package me.xianglun.blinkblinkbeachadmin.ui.event

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import me.xianglun.blinkblinkbeachadmin.data.model.Event
import me.xianglun.blinkblinkbeachadmin.data.model.Response
import me.xianglun.blinkblinkbeachadmin.data.repository.event.EventRepository
import me.xianglun.blinkblinkbeachadmin.data.repository.event.Events
import me.xianglun.blinkblinkbeachadmin.util.APIState
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val repository: EventRepository,
) : ViewModel() {

    private val _pastEvents = MutableLiveData<Events>()
    val pastEvents: LiveData<Events> = _pastEvents

    private val _futureEvents = MutableLiveData<Events>()
    val futureEvents: LiveData<Events> = _futureEvents

    private val apiStateChannel = Channel<APIState>()
    val apiState = apiStateChannel.receiveAsFlow()

    init {
        getAllEvents()
    }

    private fun getAllEvents() = viewModelScope.launch {
        repository.getAllEventsFromFirestore().collect { response ->
            if (response is Response.Success) {
                val allEventList = response.data
                val pastEvents = mutableListOf<Event>()
                val futureEvents = mutableListOf<Event>()
                for (event in allEventList) {
                    if (event.endTimeMillis <= System.currentTimeMillis()) {
                        pastEvents.add(event)
                    } else {
                        futureEvents.add(event)
                    }
                }
                _pastEvents.value = pastEvents
                _futureEvents.value = futureEvents
            }
        }
    }

    fun onSendCertButtonClicked() = viewModelScope.launch {
        apiStateChannel.send(APIState.Loading)
        apiStateChannel.send(repository.sendCertificate())
    }
}
package me.xianglun.blinkblinkbeachadmin.ui.eventDetail

import android.widget.ImageView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import me.xianglun.blinkblinkbeachadmin.data.repository.eventDetail.EventDetailRepository
import me.xianglun.blinkblinkbeachadmin.util.APIState
import javax.inject.Inject

@HiltViewModel
class EventDetailViewModel @Inject constructor(
    private val repository: EventDetailRepository,
) : ViewModel() {

    private val apiStateChannel = Channel<APIState>()
    val apiState = apiStateChannel.receiveAsFlow()

    fun loadEventImage(imageView: ImageView, imageUrl: String) {
        Glide.with(imageView.context)
            .load(imageUrl)
            .into(imageView)
    }

    fun onDeleteEventButtonClick(eventId: String) = viewModelScope.launch {
        apiStateChannel.send(APIState.Loading)
        apiStateChannel.send(repository.deleteEvent(eventId))
    }
}
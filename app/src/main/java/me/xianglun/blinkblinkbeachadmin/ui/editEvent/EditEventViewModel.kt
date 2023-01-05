package me.xianglun.blinkblinkbeachadmin.ui.editEvent

import android.net.Uri
import androidx.lifecycle.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import me.xianglun.blinkblinkbeachadmin.data.model.Event
import me.xianglun.blinkblinkbeachadmin.data.repository.editEvent.EditEventRepository
import me.xianglun.blinkblinkbeachadmin.util.APIState
import me.xianglun.blinkblinkbeachadmin.util.APIStateWithValue
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class EditEventViewModel @Inject constructor(
    private val repository: EditEventRepository,
) : ViewModel() {

    private val _day = MutableLiveData(0)
    val day: LiveData<Int> = _day

    private val _month = MutableLiveData(0)
    val month: LiveData<Int> = _month

    private val _year = MutableLiveData(0)
    val year: LiveData<Int> = _year

    private val _startHour = MutableLiveData(0)
    val startHour: LiveData<Int> = _startHour

    private val _startMinute = MutableLiveData(0)
    val startMinute: LiveData<Int> = _startMinute

    private val _endHour = MutableLiveData(0)
    val endHour: LiveData<Int> = _endHour

    private val _endMinute = MutableLiveData(0)
    val endMinute: LiveData<Int> = _endMinute

    private val _eventName = MutableLiveData("")
    val eventName: LiveData<String> = _eventName

    private val _imageUrl = MutableLiveData("")
    val imageUrl: LiveData<String> = _imageUrl

    private val imageApiStateChannel = Channel<APIStateWithValue<String>>()
    val imageUrlApiState = imageApiStateChannel.receiveAsFlow()

    private val eventApiStateChannel = Channel<APIStateWithValue<Event>>()
    val eventApiState = eventApiStateChannel.receiveAsFlow()

    private val _longitude = MutableLiveData(0.0)
    private val _latitude = MutableLiveData(0.0)

    val latLngName = MediatorLiveData<Pair<LatLng, String>>()

    init {
        latLngName.apply {
            addSource(_latitude) { latitude ->
                latLngName.value =
                    Pair(LatLng(latitude, _longitude.value ?: 0.0), _eventName.value ?: "")
            }
            addSource(_longitude) { longitude ->
                latLngName.value =
                    Pair(LatLng(_latitude.value ?: 0.0, longitude), _eventName.value ?: "")
            }
            addSource(_eventName) { eventName ->
                latLngName.value =
                    Pair(LatLng(_latitude.value ?: 0.0, _longitude.value ?: 0.0), eventName ?: "")
            }
        }
    }


    fun initializeDateTime(startTimeMillis: Long, endTimeMillis: Long) {
        val startTimeCalendar = Calendar.getInstance()
        startTimeCalendar.timeInMillis = startTimeMillis

        val endTimeCalendar = Calendar.getInstance()
        endTimeCalendar.timeInMillis = endTimeMillis

        _day.value = startTimeCalendar.get(Calendar.DAY_OF_MONTH)
        _month.value = startTimeCalendar.get(Calendar.MONTH)
        _year.value = startTimeCalendar.get(Calendar.YEAR)
        _startHour.value = startTimeCalendar.get(Calendar.HOUR)
        _startMinute.value = startTimeCalendar.get(Calendar.MINUTE)
        _endHour.value = endTimeCalendar.get(Calendar.HOUR)
        _endMinute.value = endTimeCalendar.get(Calendar.MINUTE)
    }

    fun initializeLatLng(latitude: Double, longitude: Double) {
        _latitude.value = latitude
        _longitude.value = longitude
    }

    fun initializeEventName(name: String) {
        _eventName.value = name
    }

    fun initializeEventImageUrl(imageUrl: String) {
        _imageUrl.value = imageUrl
    }

    fun onEventNameEditTextTextChange(text: CharSequence?) {
        _eventName.value = text.toString()
    }

    fun onLatitudeEditTextTextChange(latitude: CharSequence?) {
        _latitude.value = latitude.toString().toDoubleOrNull() ?: 0.0
    }

    fun onLongitudeEditTextTextChange(longitude: CharSequence?) {
        _longitude.value = longitude.toString().toDoubleOrNull() ?: 0.0
    }

    fun onDateSet(dateEditText: TextInputEditText, year: Int, month: Int, dayOfMonth: Int) {
        _year.value = year
        _month.value = month
        _day.value = dayOfMonth

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        dateEditText.setText(convertCalendarToDateString(calendar))
    }

    fun onStartTimeSet(startTimeEditText: TextInputEditText, hourOfDay: Int, minute: Int) {
        _startHour.value = hourOfDay
        _startMinute.value = minute

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)

        startTimeEditText.setText(convertCalendarToTimeString(calendar))
    }

    fun onEndTimeSet(endTimeEditText: TextInputEditText, hourOfDay: Int, minute: Int) {
        _endHour.value = hourOfDay
        _endMinute.value = minute

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)

        endTimeEditText.setText(convertCalendarToTimeString(calendar))
    }

    private fun convertCalendarToDateString(calendar: Calendar): String {
        val dateFormat = SimpleDateFormat("dd MMM yyyy")
        return dateFormat.format(calendar.time)
    }

    private fun convertCalendarToTimeString(calendar: Calendar): String {
        val dateFormat = SimpleDateFormat("hh:mm aa")
        return dateFormat.format(calendar.time)
    }

    fun uploadEventImage(fileUri: Uri) = viewModelScope.launch {
        imageApiStateChannel.send(APIStateWithValue.Loading)
        val apiState = repository.uploadEventImageToStorage(fileUri)
        if (apiState is APIStateWithValue.Success) {
            _imageUrl.value = apiState.result
        }
        imageApiStateChannel.send(apiState)
    }

    fun saveEventOnFirestore(event: Event) = viewModelScope.launch {
        eventApiStateChannel.send(APIStateWithValue.Loading)
        val apiState = repository.saveEventOnFirestore(event)
        eventApiStateChannel.send(apiState)
    }
}
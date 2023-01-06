package me.xianglun.blinkblinkbeachadmin.ui.reportDetail

import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import me.xianglun.blinkblinkbeachadmin.data.model.User
import me.xianglun.blinkblinkbeachadmin.data.repository.reportDetail.ReportDetailRepository
import me.xianglun.blinkblinkbeachadmin.util.APIState
import me.xianglun.blinkblinkbeachadmin.util.APIStateWithValue
import javax.inject.Inject

@HiltViewModel
class ReportDetailViewModel @Inject constructor(
    private val reportDetailRepository: ReportDetailRepository
) : ViewModel() {
    private val _reporter = MutableLiveData<APIStateWithValue<User>>()
    private val apiStateChannel = Channel<APIState>()
    val apiState = apiStateChannel.receiveAsFlow()

    val reporter: LiveData<APIStateWithValue<User>> = _reporter
    fun fillReporterName(reporterId: String) =  viewModelScope.launch {
        _reporter.value = reportDetailRepository.getReporter(reporterId)
    }

    fun rejectEvent(reportId: String) = viewModelScope.launch {
        apiStateChannel.send(APIState.Loading)
        apiStateChannel.send(reportDetailRepository.rejectReport(reportId))
    }
}
package me.xianglun.blinkblinkbeachadmin.ui.report

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import me.xianglun.blinkblinkbeachadmin.data.model.Report
import me.xianglun.blinkblinkbeachadmin.data.repository.report.ReportRepository
import me.xianglun.blinkblinkbeachadmin.util.APIStateWithValue
import me.xianglun.blinkblinkbeachadmin.util.ReportStatus
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val repository: ReportRepository
) : ViewModel() {

    private val _reportList = MutableLiveData<APIStateWithValue<List<Report>>>()
    val reportList: LiveData<APIStateWithValue<List<Report>>> = _reportList

    fun populateReportList(status: ReportStatus) = viewModelScope.launch {
        _reportList.value = repository.fetchReportList(status)
    }

    fun clearCurrentReportList() = viewModelScope.launch {
        _reportList.value = APIStateWithValue.Success(emptyList())
    }
}
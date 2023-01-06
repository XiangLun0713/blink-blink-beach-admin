package me.xianglun.blinkblinkbeachadmin.data.repository.report

import android.net.Uri
import me.xianglun.blinkblinkbeachadmin.data.model.Report
import me.xianglun.blinkblinkbeachadmin.util.APIStateWithValue
import me.xianglun.blinkblinkbeachadmin.util.ReportStatus

interface ReportRepository {
    suspend fun fetchReportList(status: ReportStatus): APIStateWithValue<List<Report>>
}
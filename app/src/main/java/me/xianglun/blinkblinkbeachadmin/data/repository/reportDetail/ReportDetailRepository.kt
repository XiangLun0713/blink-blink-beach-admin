package me.xianglun.blinkblinkbeachadmin.data.repository.reportDetail

import me.xianglun.blinkblinkbeachadmin.data.model.User
import me.xianglun.blinkblinkbeachadmin.util.APIState
import me.xianglun.blinkblinkbeachadmin.util.APIStateWithValue

interface ReportDetailRepository {
    suspend fun getReporter(reporterId: String): APIStateWithValue<User>
    suspend fun rejectReport(reportId: String): APIState
}
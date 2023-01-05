package me.xianglun.blinkblinkbeachadmin.data.repository.report

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import me.xianglun.blinkblinkbeachadmin.data.model.Report
import me.xianglun.blinkblinkbeachadmin.util.APIStateWithValue
import me.xianglun.blinkblinkbeachadmin.util.ReportStatus
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storageReference: StorageReference,
    private val firebaseStorage: FirebaseStorage,
) : ReportRepository {

    val reportsCollection = firestore.collection("reports")

    override suspend fun fetchReportList(status: ReportStatus): APIStateWithValue<List<Report>> {
        val snapshot = reportsCollection.whereEqualTo("status", status)
        val reportList = mutableListOf<Report>()

        return try {
            val reportListSnapshot: QuerySnapshot =
                snapshot.get().await()
            for (reportSnapshot in reportListSnapshot) {
                val report = reportSnapshot.toObject(Report::class.java)
                report.id = reportSnapshot.id
                reportList.add(report)
            }
            APIStateWithValue.Success(reportList.sortedBy { it.date })
        } catch (e: Exception) {
            APIStateWithValue.Error("${e.message}")
        }
    }
}
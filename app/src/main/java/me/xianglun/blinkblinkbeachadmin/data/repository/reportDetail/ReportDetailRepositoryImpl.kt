package me.xianglun.blinkblinkbeachadmin.data.repository.reportDetail

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import me.xianglun.blinkblinkbeachadmin.data.model.User
import me.xianglun.blinkblinkbeachadmin.util.APIState
import me.xianglun.blinkblinkbeachadmin.util.APIStateWithValue
import me.xianglun.blinkblinkbeachadmin.util.ReportStatus
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportDetailRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
) : ReportDetailRepository{

    val userCollection = firestore.collection("users")
    val reportCollection = firestore.collection("reports")

    override suspend fun getReporter(reporterId: String): APIStateWithValue<User> {
        var reporter = User()
        return try {
            val reporterSnapshot = userCollection.document(reporterId).get().addOnSuccessListener {
                reporter = it.toObject(User::class.java)!!
            }.await()
            APIStateWithValue.Success(reporter)
        } catch (e: Exception) {
            APIStateWithValue.Error("${e.message}")
        }
    }

    override suspend fun rejectReport(reportId: String): APIState {
        return try{
            reportCollection.document(reportId).update("status", ReportStatus.REJECTED).await()
            APIState.Success
        }catch (e: Exception){
            APIState.Error(e.message)
        }

    }


}
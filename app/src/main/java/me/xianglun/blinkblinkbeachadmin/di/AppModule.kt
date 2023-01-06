package me.xianglun.blinkblinkbeachadmin.di

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.xianglun.blinkblinkbeachadmin.data.repository.createEvent.CreateEventRepository
import me.xianglun.blinkblinkbeachadmin.data.repository.createEvent.CreateEventRepositoryImpl
import me.xianglun.blinkblinkbeachadmin.data.repository.editEvent.EditEventRepository
import me.xianglun.blinkblinkbeachadmin.data.repository.editEvent.EditEventRepositoryImpl
import me.xianglun.blinkblinkbeachadmin.data.repository.event.EventRepository
import me.xianglun.blinkblinkbeachadmin.data.repository.event.EventRepositoryImpl
import me.xianglun.blinkblinkbeachadmin.data.repository.eventDetail.EventDetailRepository
import me.xianglun.blinkblinkbeachadmin.data.repository.eventDetail.EventDetailRepositoryImpl
import me.xianglun.blinkblinkbeachadmin.data.repository.report.ReportRepository
import me.xianglun.blinkblinkbeachadmin.data.repository.report.ReportRepositoryImpl
import me.xianglun.blinkblinkbeachadmin.data.repository.reportDetail.ReportDetailRepository
import me.xianglun.blinkblinkbeachadmin.data.repository.reportDetail.ReportDetailRepositoryImpl
import me.xianglun.blinkblinkbeachadmin.data.repository.user.UserRepository
import me.xianglun.blinkblinkbeachadmin.data.repository.user.UserRepositoryImpl
import me.xianglun.blinkblinkbeachadmin.data.repository.userDetail.UserDetailRepository
import me.xianglun.blinkblinkbeachadmin.data.repository.userDetail.UserDetailRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideStorageRef() = Firebase.storage.reference

    @Provides
    @Singleton
    fun provideFirebaseStorage() = FirebaseStorage.getInstance()

    @Provides
    @Singleton
    fun provideEventRepository(eventRepositoryImpl: EventRepositoryImpl): EventRepository =
        eventRepositoryImpl

    @Provides
    @Singleton
    fun provideEditEventRepository(editEventRepositoryImpl: EditEventRepositoryImpl): EditEventRepository =
        editEventRepositoryImpl

    @Provides
    @Singleton
    fun provideEventDetailRepository(eventDetailRepositoryImpl: EventDetailRepositoryImpl): EventDetailRepository =
        eventDetailRepositoryImpl

    @Provides
    @Singleton
    fun provideReportRepository(reportRepositoryImpl: ReportRepositoryImpl): ReportRepository =
        reportRepositoryImpl

    @Provides
    @Singleton
    fun provideReportDetailRepository(reportDetailRepositoryImpl: ReportDetailRepositoryImpl): ReportDetailRepository =
        reportDetailRepositoryImpl

    @Provides
    @Singleton
    fun provideCreateEventRepository(createEventRepositoryImpl: CreateEventRepositoryImpl): CreateEventRepository =
        createEventRepositoryImpl

    @Provides
    @Singleton
    fun provideUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository =
        userRepositoryImpl

    @Provides
    @Singleton
    fun provideUserDetailRepository(userDetailRepositoryImpl: UserDetailRepositoryImpl): UserDetailRepository =
        userDetailRepositoryImpl
}
package me.xianglun.blinkblinkbeachadmin.di

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.xianglun.blinkblinkbeachadmin.data.repository.event.EventRepository
import me.xianglun.blinkblinkbeachadmin.data.repository.event.EventRepositoryImpl
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

}
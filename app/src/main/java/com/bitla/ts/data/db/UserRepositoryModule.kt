package com.bitla.ts.data.db

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@InstallIn(ActivityRetainedComponent::class)
@Module
object UserRepositoryModule {

    @Provides
    fun providesUserRepository(userDao: UserDao) = UserRepositoryImpl(userDao)
}
package com.bitla.ts.data.db

import com.bitla.ts.domain.pojo.user.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun insertUser(user: User): Flow<Long>

    suspend fun getAllUsers(): Flow<List<User>>

    suspend fun getCurrentUser(): Flow<User>

    suspend fun deleteUser(user: User): Flow<Int>

}
package com.bitla.ts.data.db

import com.bitla.ts.domain.pojo.user.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(private val userDao: UserDao): UserRepository {

    override suspend fun insertUser(user: User): Flow<Long>  = flow {
        emit(userDao.insertUser(user))
    }

    override suspend fun getAllUsers(): Flow<List<User>> = flow {
        emit(userDao.getAllUsers())
    }

    override suspend fun getCurrentUser(): Flow<User> = flow {
        emit(userDao.getCurrentUser())
    }

    override suspend fun deleteUser(user: User): Flow<Int>  = flow {
        emit(userDao.deleteUser(user))
    }

}
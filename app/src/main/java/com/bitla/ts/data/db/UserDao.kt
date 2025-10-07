package com.bitla.ts.data.db

import androidx.room.*
import com.bitla.ts.domain.pojo.user.*
import com.bitla.ts.utils.constants.*

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User): Long

    @Query("SELECT * FROM $USERS_TABLE_NAME ORDER BY `current_timestamp` DESC")
    fun getAllUsers(): List<User>

    @Query("SELECT * FROM $USERS_TABLE_NAME ORDER BY `current_timestamp` DESC LIMIT 1")
    fun getCurrentUser(): User

    @Delete
    fun deleteUser(user: User): Int

}
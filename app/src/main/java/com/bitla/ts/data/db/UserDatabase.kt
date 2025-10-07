package com.bitla.ts.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import com.bitla.ts.domain.pojo.user.User

@Database(
    entities = [User::class],
    version = 4,
    exportSchema = false
)
//@TypeConverters(SourceConverter::class)
abstract class UserDatabase: RoomDatabase() {
    abstract fun getUserDao(): UserDao
}
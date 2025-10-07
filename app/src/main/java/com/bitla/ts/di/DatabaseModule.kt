package com.bitla.ts.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bitla.ts.data.db.UserDatabase
import com.bitla.ts.utils.constants.DB_NAME
import com.bitla.ts.utils.constants.USERS_TABLE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun providesUserDatabase(@ApplicationContext context: Context): UserDatabase {
        /*val MIGRATION_1_2 = object: Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE users_table ADD COLUMN is_fingerprint_linked INTEGER"
                )
            }

        }
*/
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add the new column with a default value of false
                database.execSQL("ALTER TABLE ${USERS_TABLE_NAME} ADD COLUMN is_encryption_enabled INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add the new column with a default value of false
                database.execSQL("ALTER TABLE ${USERS_TABLE_NAME} ADD COLUMN city_name TEXT NOT NULL DEFAULT ''")
            }
        }
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE ${USERS_TABLE_NAME} ADD COLUMN shift_name TEXT")
                database.execSQL("ALTER TABLE ${USERS_TABLE_NAME} ADD COLUMN counter_name TEXT")
            }
        }

        return Room.databaseBuilder(context, UserDatabase::class.java, DB_NAME).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4).build()
    }

    @Singleton
    @Provides
    fun providesUserDao(userDatabase: UserDatabase) = userDatabase.getUserDao()
}
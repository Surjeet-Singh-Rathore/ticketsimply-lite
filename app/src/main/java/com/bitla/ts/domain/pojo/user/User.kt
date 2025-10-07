package com.bitla.ts.domain.pojo.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.bitla.ts.utils.constants.USERS_TABLE_NAME

@Entity(
    tableName = USERS_TABLE_NAME,
    primaryKeys = ["domain_name", "user_id"]
)
data class User(

    @ColumnInfo(name = "username", defaultValue = "")
    var username: String? = "",
    @ColumnInfo(name = "password", defaultValue = "")
    var password: String = "",
    @ColumnInfo(name = "domain_name", defaultValue = "")
    var domainName: String = "",
    @ColumnInfo(name = "current_timestamp")
    var currentTimeStamp: Long = 0L,
    @ColumnInfo(name = "name", defaultValue = "")
    var name: String? = "",
    @ColumnInfo(name = "api_key", defaultValue = "")
    var apiKey: String? = "",
    @ColumnInfo(name = "user_id", defaultValue = "0")
    var userId: Int = 0,
    @ColumnInfo(name = "travels_name", defaultValue = "")
    var travelsName: String = "",
    @ColumnInfo(name = "language", defaultValue = "")
    var language: String = "",
    @ColumnInfo(name = "phone_number")
    var phoneNumber: String? = "",
    @ColumnInfo(name = "email")
    var email: String? = "",
    @ColumnInfo(name = "logo_url", defaultValue = "")
    var logoUrl: String = "",
    @ColumnInfo(name = "trackingo_api_key", defaultValue = "")
    var trackingoApiKey: String = "",
    @ColumnInfo(name = "trackingo_url", defaultValue = "")
    var trackingoUrl: String = "",
    @ColumnInfo(name = "role", defaultValue = "")
    var role: String? = "",
    @ColumnInfo(name = "account_balance", defaultValue = "")
    var accountBalance: String = "",
    @ColumnInfo(name = "city_id", defaultValue = "")
    var cityId: String = "",
    @ColumnInfo(name = "city_name", defaultValue = "")
    var cityName: String = "",
    @ColumnInfo(name = "header", defaultValue = "")
    var header: String = "",
    @ColumnInfo(name = "is_encryption_enabled")
    var isEncryptionEnabled: Boolean = false,
    @ColumnInfo(name = "shift_name")
    var shiftName: String?,
    @ColumnInfo(name = "counter_name")
    var counterName: String?

)
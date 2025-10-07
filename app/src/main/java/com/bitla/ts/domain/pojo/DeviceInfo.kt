package com.bitla.ts.domain.pojo

data class DeviceInfo(
    val android_os: String,
    val android_version: String,
    val app_domain: String,
    val app_language: String,
    val app_package_name: String,
    val app_user_login: String,
    val app_version: String,
    val app_version_code: Int,
    val device_date: String,
    val device_info: String,
    val device_language: String
)
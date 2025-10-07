package com.bitla.ts.domain.pojo.location_logs.request

data class LocationLogRequest(
    val api_key: String,
    val bus_no: String,
    val description: String,
    val latitude: String,
    val locale: String,
    val longitude: String,
    val time_stamp: String
)
package com.bitla.ts.domain.pojo.your_bus_location

data class Data(
    val ac_status: String,
    val address: String,
    val gps_id: String,
    val gps_time: String,
    val ignition_off: String,
    val last_fetch_time: String,
    var lat: String,
    val lat_long: String,
    var long: String,
    val orientation_degree: String,
    val running_status: String,
    val speed: String,
    val status_change_timestamp: String,
    val timeinticks: String
)
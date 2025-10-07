package com.bitla.ts.domain.pojo.allotedServiceDirect.AllotedDirctResponse

data class PickuVanService(
    val booked_seats: Int,
    val city: String,
    val coach_number: String,
    val departure_time: String,
    val pickup_van_no: String,
    val schedule_id: Int,
    val travel_date: String
)
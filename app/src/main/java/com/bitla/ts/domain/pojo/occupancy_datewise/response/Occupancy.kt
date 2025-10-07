package com.bitla.ts.domain.pojo.occupancy_datewise.response


import com.google.gson.annotations.SerializedName

data class Occupancy(
    @SerializedName("date")
    val date: String?,
    @SerializedName("is_coach_change")
    val isCoachChange: Boolean?,
    @SerializedName("is_inactive_service")
    val isInactiveService: Boolean?,
    @SerializedName("occupied_seats")
    val occupiedSeats: Int?,
    @SerializedName("available_seats")
    val availableSeats: Int?,
    @SerializedName("total_seats")
    val totalSeats: Int?,
    @SerializedName("reservation_id")
    val reservationId: Long?,
    var serviceId: Int?,
    var serviceName: String?,
    var coachType: String?,
    var deptTime: String?,

    @SerializedName("origin")
    var origin: String?,
    @SerializedName("origin_id")
    var originId: String?,
    @SerializedName("destination")
    var destination: String?,
    @SerializedName("destination_id")
    var destinationId: String?,
    @SerializedName("bus_type")
    var busType: String?
)
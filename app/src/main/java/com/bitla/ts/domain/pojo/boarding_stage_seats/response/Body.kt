package com.bitla.ts.domain.pojo.boarding_stage_seats.response


import com.google.gson.annotations.SerializedName

data class Body(
    @SerializedName("arr_date")
    val arrDate: String?,
    @SerializedName("arr_time")
    val arrTime: String?,
    @SerializedName("available_seats")
    val availableSeats: Int?,
    @SerializedName("bus_type")
    val busType: String?,
    @SerializedName("coach_details")
    val coachDetails: CoachDetails?,
    @SerializedName("dep_date")
    val depDate: String?,
    @SerializedName("dep_time")
    val depTime: String?,
    @SerializedName("destination")
    val destination: Destination?,
    @SerializedName("duration")
    val duration: String?,
    @SerializedName("is_own_route")
    val isOwnRoute: Boolean?,
    @SerializedName("is_service_blocked")
    val isServiceBlocked: Boolean?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("number")
    val number: String?,
    @SerializedName("origin")
    val origin: Origin?,
    @SerializedName("reservation_id")
    val reservationId: Int?,
    @SerializedName("route_id")
    val routeId: Int?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("travel_date")
    val travelDate: String?
)
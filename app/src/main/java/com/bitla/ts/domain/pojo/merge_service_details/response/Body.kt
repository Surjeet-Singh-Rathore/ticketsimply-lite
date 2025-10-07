package com.bitla.ts.domain.pojo.merge_service_details.response


import com.google.gson.annotations.SerializedName

data class Body(
    @SerializedName("arr_date")
    val arrDate: String?,
    @SerializedName("arr_time")
    val arrTime: String?,
    @SerializedName("available_seats")
    val availableSeats: Int?,
    @SerializedName("booking_type_seat_counts")
    val bookingTypeSeatCounts: BookingTypeSeatCounts?,
    @SerializedName("coach_details")
    val coachDetails: CoachDetails?,
    @SerializedName("dep_date")
    val depDate: String?,
    @SerializedName("dep_time")
    val depTime: String?,
    @SerializedName("destination")
    val destination: Destination?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("number")
    val number: String?,
    @SerializedName("origin")
    val origin: Origin?,
    @SerializedName("route_id")
    val routeId: Int?,
    @SerializedName("travel_date")
    val travelDate: String?
)
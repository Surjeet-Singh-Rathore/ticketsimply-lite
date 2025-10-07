package com.bitla.ts.domain.pojo.merge_bus_seat_mapping.response


import com.google.gson.annotations.SerializedName

data class SeatMappingDetail(
    @SerializedName("booking_source")
    val bookingSource: String?,
    @SerializedName("destination")
    val destination: String?,
    @SerializedName("passenger_contact_number")
    val passengerContactNumber: String?,
    @SerializedName("pnr_number")
    val pnrNumber: String?,
    @SerializedName("seats")
    val seats: List<Seat?>?,
    @SerializedName("source")
    val source: String?,
    @SerializedName("amount")
    var amount: String?,
    @SerializedName("pay_status")
    var payStatus: String?,
    @SerializedName("is_disabled")
    var isDisabled: Boolean?
)
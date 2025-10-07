package com.bitla.ts.domain.pojo.rapid_booking.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RapidBookingRequest{
    @SerializedName("reservation_id")
    @Expose
    var reservationId: String? = null

    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null


    @SerializedName("origin_id")
    @Expose
    var originId: String? = null

    @SerializedName("destination_id")
    @Expose
    var destinationId: String? = null

    @SerializedName("is_offline_booked_tic")
    @Expose
    var isOfflineBookedTic: Boolean? = false

    @SerializedName("no_of_seats")
    @Expose
    var noOfSeats: String? = null

    @SerializedName("is_from_bus_opt_app")
    @Expose
    var isFromBusOptApp: Boolean? = null

    @SerializedName("is_rapid_booking")
    @Expose
    var isRapidBooking: Boolean =false

    @SerializedName("seat_numbers")
    var seatNumbers: String? = null

    @SerializedName("operator_api_key")
    @Expose
    var operatorApiKey: String? = null

    @SerializedName("locale")
    @Expose
    var locale: String? = null

    @SerializedName("response_format")
    @Expose
    var responseFormat: Boolean? = false

    @SerializedName("edited_seat_fare")
    @Expose
    var editedSeatFare: String? = ""

    @SerializedName("device_info")
    var deviceInfo: String? = null

    @SerializedName("drop_off")
    var dropOff: String? = null

    @SerializedName("boarding_at")
    var boardingAt: String? = null
}

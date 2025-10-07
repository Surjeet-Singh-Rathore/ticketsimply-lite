package com.bitla.ts.domain.pojo.book_extra_seat.request

import com.bitla.ts.domain.pojo.book_ticket_full.request.PassengerGstDetails
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ReqBody {
    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    @SerializedName("reservation_id")
    @Expose
    var reservationId: String? = null

    @SerializedName("origin_id")
    @Expose
    var originId: String? = null

    @SerializedName("destination_id")
    @Expose
    var destinationId: String? = null

    @SerializedName("boarding_at")
    @Expose
    var boardingAt: String? = null

    @SerializedName("drop_off")
    @Expose
    var dropOff: String? = null

    @SerializedName("no_of_seats")
    @Expose
    var noOfSeats: String? = null

    @SerializedName("contact_detail")
    @Expose
    var contactDetail: ContactDetail? = null

    @SerializedName("seat_details")
    @Expose
    var seatDetails: List<SeatDetail>? = null

    @SerializedName("booking_details")
    @Expose
    var bookingDetails: BookingDetails? = null

    @SerializedName("is_from_middle_tier")
    @Expose
    val isFromMiddleTier: Boolean = true

    @SerializedName("locale")
    @Expose
    var locale: String? = ""

    @SerializedName("passenger_gst_details")
    var passengerGstDetails: PassengerGstDetails? = null

    @SerializedName("device_info")
    var deviceInfo: String? = null

    @SerializedName("is_bima_ticket")
    @Expose
    var isBimaTicket: Boolean? = false

    @SerializedName("operator_api_key")
    @Expose
    var operatorApiKey: String? = null

    @SerializedName("departure_date")
    @Expose
    var departureDate: String? = null


//    @SerializedName("is_rapid_booking")
//    @Expose
//    var isRapidBooking: String? = null

    @SerializedName("auth_pin")
    @Expose
    var authPin: String? = ""
}
package com.bitla.ts.domain.pojo.book_with_extra_seat.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BookTicketWithExtraSeatRequest(

    @SerializedName("format")
    var format: String? = null,

    @SerializedName("json_format")
    var jsonFormat: String? = null,

    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null,

    @SerializedName("is_bima_ticket")
    @Expose
    var is_bima_ticket: String? = "false",

    @SerializedName("operator_api_key")
    @Expose
    var operator_api_key: String? = null,

    @SerializedName("is_from_bus_opt_app")
    @Expose
    var is_from_bus_opt_app: String? = null,

    @SerializedName("reservation_id")
    @Expose
    var reservationId: String? = null,

    @SerializedName("origin_id")
    @Expose
    var originId: String? = null,

    @SerializedName("destination_id")
    @Expose
    var destinationId: String? = null,

    @SerializedName("boarding_at")
    @Expose
    var boardingAt: String? = null,

    @SerializedName("drop_off")
    @Expose
    var dropOff: String? = null,

    @SerializedName("no_of_seats")
    @Expose
    var noOfSeats: String? = null,

    @SerializedName("locale")
    @Expose
    var locale: String? = "",

    @SerializedName("booking_details")
    @Expose
    val bookingDetails: BookingDetails,

    @SerializedName("contact_detail")
    @Expose
    val contactDetail: ContactDetail,

    @SerializedName("coupon_details")
    @Expose
    val couponDetails: List<Any>? = null,

    @SerializedName("extra_seats")
    @Expose
    val extraSeats: ExtraSeats? = null,

    @SerializedName("package_details_id")
    @Expose
    val packageDetailsId: PackageDetailsId? = null,

    @SerializedName("seat_details")
    @Expose
    val seatDetails: MutableList<SeatDetail>
)
package com.bitla.ts.domain.pojo.book_ticket_full.request

import com.bitla.ts.domain.pojo.quick_book_chile.request.quickbook_book_ticket_req.SelectedSeatType
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ReqBodyWithInsurance {
    @SerializedName("reservation_id")
    @Expose
    var reservationId: String? = null

    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    @SerializedName("operator_api_key")
    @Expose
    var operatorApiKey: String? = null

    @SerializedName("locale")
    @Expose
    var locale: String? = null

    @SerializedName("origin_id")
    @Expose
    var originId: String? = null

    @SerializedName("destination_id")
    @Expose
    var destinationId: String? = null

    @SerializedName("boarding_at")
    @Expose
    var boardingAt: String? = null

    @SerializedName("format")
    @Expose
    var format: String? = null

    @SerializedName("drop_off")
    @Expose
    var dropOff: String? = null

    @SerializedName("no_of_seats")
    @Expose
    var noOfSeats: String? = null

    @SerializedName("seat_details")
    @Expose
    var seatDetails: MutableList<SeatDetail>? = null

    @SerializedName("contact_detail")
    @Expose
    var contactDetail: ContactDetail? = null

    @SerializedName("coupon_details")
    @Expose
    var couponDetails: List<Any>? = null

    @SerializedName("booking_details")
    @Expose
    var bookingDetails: BookingDetails? = null

    @SerializedName("partial_payment_details")
    @Expose
    var partialPaymentDetails: PartialPaymentDetails? = null


    @SerializedName("is_from_bus_opt_app")
    @Expose
    var isFromBusOptApp: String? = null

    @SerializedName("is_rapid_booking")
    @Expose
    var isRapidBooking: String? = null

    @SerializedName("is_from_middle_tier")
    @Expose
    var isFromMiddleTier: String? = null

    @SerializedName("is_quick_booking")
    @Expose
    var isQuickBooking: String? = null

    @SerializedName("package_details_id")
    @Expose
    var packageDetailsId: PackageDetailsId? = null

    @SerializedName("coupon_code")
    @Expose
    var couponCode: String? = null

    @SerializedName("selected_seat_types")
    var selectedSeatTypes: MutableList<SelectedSeatType> ? = null

    @SerializedName("response_format")
    var responseFormat: String? = null

    @SerializedName("is_insurance_enabled")
    @Expose
    var isInsuranceEnabled: Boolean = false

    @SerializedName("device_info")
    var deviceInfo: String? = null

}
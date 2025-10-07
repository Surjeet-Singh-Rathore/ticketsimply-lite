package com.bitla.ts.domain.pojo.view_reservation


import com.google.gson.annotations.SerializedName

data class PassengerDetail(
    @SerializedName("actual_travel_date")
    var actualTravelDate: String,
    @SerializedName("boarding_city")
    var boardingCity: String,
    @SerializedName("booked_by")
    var bookedBy: String,
    @SerializedName("booking_type")
    var bookingType: String,
    @SerializedName("destination")
    var destination: Int,
    @SerializedName("dropping_city")
    var droppingCity: String,
    @SerializedName("dropping_point")
    var droppingPoint: String,
    @SerializedName("dropping_point_time")
    var droppingPointTime: String,
    @SerializedName("gst_amount")
    var gstAmount: Double,
    @SerializedName("id_card")
    var idCard: IdCard,
    @SerializedName("is_phone_booking")
    var isPhoneBooking: Boolean,
    @SerializedName("landmark")
    var landmark: String,
    @SerializedName("onbehalf_travel_branch_id")
    var onbehalfTravelBranchId: Int,
    @SerializedName("origin")
    var origin: Int,
    @SerializedName("passenger_age")
    var passengerAge: Int,
    @SerializedName("passenger_name")
    var passengerName: String,
    @SerializedName("phone_number")
    var phoneNumber: String,
    @SerializedName("pnr_number")
    var pnrNumber: String,
    @SerializedName("remarks")
    var remarks: String,
    @SerializedName("seat_number")
    var seatNumber: String,
    @SerializedName("sex")
    var sex: String,
    @SerializedName("stage_dep_time")
    var stageDepTime: String,
    @SerializedName("stage_name")
    var stageName: String,
    @SerializedName("status")
    var status: Int,
    @SerializedName("temperature")
    var temperature: String,
    @SerializedName("ticket_fare")
    var ticketFare: Double? = null,
    @SerializedName("if_selected")
    var ifSelected: Boolean = false,
    @SerializedName("new_seat")
    var newSeat: String? = null,
    @SerializedName("total_trip")
    var totalTrip: Int? = null,
    @SerializedName("booking_src_image")
    var bookingSrcImage: String? = null,
    @SerializedName("is_pay_at_bus_ticket")
    var isPayAtBus: Boolean? = false,
    @SerializedName("is_boarded")
    var isBoarded: Boolean? = false,
    @SerializedName("onbehalf_of_booked_by_user_or_agent")
    var onBehalfOfBookedByUserOrAgent: String? = null,
    @SerializedName("boarded_status")
    var boardedStatus: Boolean = false,
    @SerializedName("is_partially_booked")
    var isPartiallyBooked: Boolean = false,
    @SerializedName("trip_counts")
    var tripCounts: Int? = null,


)
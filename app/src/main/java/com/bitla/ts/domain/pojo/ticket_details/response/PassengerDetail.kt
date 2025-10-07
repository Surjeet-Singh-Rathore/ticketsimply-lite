package com.bitla.ts.domain.pojo.ticket_details.response


import com.google.gson.annotations.SerializedName

data class PassengerDetail(
    @SerializedName("adult_fare")
    val adultFare: String? = "",
    @SerializedName("age")
    val age: Int?,
    @SerializedName("boarding_at")
    val boardingAt: Int?,
    @SerializedName("boarding_status")
    val boardingStatus: String? = "",
    @SerializedName("can_cancel")
    val canCancel: Boolean,
    @SerializedName("can_cancel_ticket_for_user")
    val canCancelTicketForUser: Boolean,
    @SerializedName("can_cancel_ticket_for_agent")
    val can_cancel_ticket_for_agent: Boolean,
    @SerializedName("can_shift_ticket")
    val canShiftTicket: Boolean,
    @SerializedName("drop_off")
    val dropOff: Int?,
    @SerializedName("email")
    val email: String? = "",
    @SerializedName("gender")
    val gender: String? = "",
    @SerializedName("mobile")
    val mobile: String? = "",
    @SerializedName("cus_mobile")
    val cusMobile: String? = "",
    @SerializedName("country_code")
    val countryCode: String? = "",
    @SerializedName("cus_email")
    val cusEmail: String? = "",
    @SerializedName("name")
    val name: String? = "",
    @SerializedName("net_fare")
    val netFare: String,
    @SerializedName("passenger_category")
    val passengerCategory: String,
    @SerializedName("seat_number")
    val seatNumber: String,
    @SerializedName("ticket_status")
    val ticketStatus: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("can_release_phone_block")
    var canReleasePhoneBlock: Boolean = false,
    @SerializedName("can_confirm_phone_block")
    var canConfirmPhoneBlock: Boolean = false,
    @SerializedName("meal_coupons")
    val meal_coupons: List<Any>,
    @SerializedName("meal_required")
    val meal_required: Boolean,
    @SerializedName("selected_meal_type")
    val selected_meal_type: String,
    @SerializedName("insurance_amount")
    val insuranceAmount: String,
    @SerializedName("is_eticket")
    val isEticket: Boolean = false,
)
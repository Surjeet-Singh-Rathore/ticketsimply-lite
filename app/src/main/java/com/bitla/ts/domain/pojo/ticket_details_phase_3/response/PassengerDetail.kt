package com.bitla.ts.domain.pojo.ticket_details_phase_3.response


import com.google.gson.annotations.SerializedName

data class PassengerDetail(
    @SerializedName("adult_fare")
    val adultFare: Double?,
    @SerializedName("age")
    val age: Int?,
    @SerializedName("boarding_at")
    val boardingAt: Int?,
    @SerializedName("boarding_status")
    val boardingStatus: String?,
    @SerializedName("cus_email")
    val cusEmail: String?,
    @SerializedName("cus_mobile")
    val cusMobile: String?,
    @SerializedName("destination_address")
    val destinationAddress: Any?,
    @SerializedName("drop_off")
    val dropOff: Int?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("gender")
    val gender: String?,
    @SerializedName("insurance_amount")
    val insuranceAmount: String?,
    @SerializedName("meal_coupons")
    val mealCoupons: List<Any?>?,
    @SerializedName("meal_required")
    val mealRequired: Boolean?,
    @SerializedName("mobile")
    val mobile: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("net_fare")
    val netFare: Double?,
    @SerializedName("origin_address")
    val originAddress: Any?,
    @SerializedName("seat_number")
    val seatNumber: String?,
    @SerializedName("selected_meal_type")
    val selectedMealType: String?,
    @SerializedName("ticket_status")
    val ticketStatus: String?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("meal_coupon_qr")
    val meal_coupon_qr: String?,
    @SerializedName("passenger_category")
    val passengerCategory: String?,
)
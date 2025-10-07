package com.bitla.ts.domain.pojo.book_ticket_full.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SeatDetail {
    @SerializedName("is_primary")
    @Expose
    var isPrimary: Boolean? = null

    @SerializedName("seat_number")
    @Expose
    var seatNumber: String? = null

    @SerializedName("sex")
    @Expose
    var sex: String? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("age")
    @Expose
    var age: String? = null

    @SerializedName("additional_fare")
    @Expose
    var additionalFare: Any? = null

    @SerializedName("discount_amount")
    @Expose
    var discountAmount: Any? = null

    @SerializedName("is_round_trip_seat")
    @Expose
    var isRoundTripSeat: Boolean? = null

    @SerializedName("passenger_category")
    @Expose
    var passengerCategory: String? = null

    @SerializedName("first_name")
    @Expose
    var firstName: String? = null

    @SerializedName("last_name")
    @Expose
    var lastName: String? = null

    @SerializedName("id_card_type")
    @Expose
    var idCardType: Int? = null

    @SerializedName("id_card_number")
    @Expose
    var idCardNumber: String? = null

    @SerializedName("passport_issued_date")
    @Expose
    var passportIssuedDate: String? = null

    @SerializedName("passport_expiry_date")
    @Expose
    var passportExpiryDate: String? = null

    @SerializedName("place_of_issue")
    @Expose
    var placeOfIssue: String? = null

    @SerializedName("nationality")
    @Expose
    var nationality: String? = null

    @SerializedName("fare")
    @Expose
    var fare: Any? = null

    @SerializedName("coupon_code")
    @Expose
    var couponCode: String? = null



    @SerializedName("meal_required")
    var mealRequired: Boolean = false
    @SerializedName("selected_meal_type")
    var selectedMealType: String? = null
}
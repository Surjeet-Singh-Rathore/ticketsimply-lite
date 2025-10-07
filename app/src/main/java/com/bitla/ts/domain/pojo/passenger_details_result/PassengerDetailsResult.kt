package com.bitla.ts.domain.pojo.passenger_details_result

import com.google.gson.annotations.SerializedName

data class PassengerDetailsResult(

    var expand: Boolean? = null,
    @SerializedName("is_primary")
    var isPrimary: Boolean = true,
    var seatNumber: String? = "",
    @SerializedName("name")
    var name: String? = "",
    @SerializedName("age")
    var age: String? = "",
    @SerializedName("sex")
    var sex: String? = "",
    @SerializedName("additional_fare")
    var additionalFare: String? = "0",
    @SerializedName("discount_amount")
    var discountAmount: String? = "",
    @SerializedName("is_round_trip_seat")
    val isRoundTripSeat: Boolean = false,
    @SerializedName("passenger_category")
    val passengerCategory: String? = "",
    @SerializedName("first_name")
    var firstName: String? = "",
    @SerializedName("last_name")
    var lastName: String? = "",
    @SerializedName("id_card_type")
    var idCardType: String? = "",
    @SerializedName("id_card_type_id")
    var idCardTypeId: Int? = 0,
    @SerializedName("id_card_number")
    var idCardNumber: String? = "",
    @SerializedName("passport_issued_date")
    var passportIssuedDate: String? = "",
    @SerializedName("passport_expiry_date")
    var passportExpiryDate: String? = "",
    @SerializedName("place_of_issue")
    var placeOfIssue: String? = "",
    @SerializedName("nationality")
    val nationality: String? = "",
    var fare: String? = "",

    @SerializedName("contact_detail")
    val contactDetail: MutableList<ContactDetail>,

    @SerializedName("is_extra_seat")
    val isExtraSeat: Boolean = false,

    @SerializedName("idnumber")
    var idnumber: String? = "",
    @SerializedName("idType")
    var idType: String? = "",
    @SerializedName("couponCode")
    var couponCode: String? = null,
    @SerializedName("meal_coupons")
    val meal_coupons: List<Any?>? = null,
    @SerializedName("meal_required")
    var mealRequired: Boolean = false,
    @SerializedName("selected_meal_type")
    var selectedMealType: String? = null,

    @SerializedName("selected_meal_type_id")
    var selectedMealTypeId: Int = 0,

    @SerializedName("edit_fare")
    var editFare: Any? = null,
    @SerializedName("base_fare_filter")
    var baseFareFilter: Any? = null,
    @SerializedName("is_meal_selected")
    var isMealSelected: Boolean = false,
    @SerializedName("seat_discount")
    var seatDiscount: Double? = null,

    var isFilled: Boolean = false,
    var isSelectedGenderMale: Boolean = false,
    var isSelectedGenderFemale: Boolean = false,
    var paxMandatoryMap: MutableMap<Any, Any> = mutableMapOf(),

    var expandedMealType: Boolean = false
)

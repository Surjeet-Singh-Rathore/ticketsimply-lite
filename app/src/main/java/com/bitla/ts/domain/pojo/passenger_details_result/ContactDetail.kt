package com.bitla.ts.domain.pojo.passenger_details_result


import com.google.gson.annotations.SerializedName

data class ContactDetail(
    @SerializedName("mobile_number")
    var mobileNumber: String? = "",
    @SerializedName("alternate_mobile_number")
    var alternateMobileNumber: String? = "",
    @SerializedName("email")
    val email: String? = "",
    @SerializedName("cus_mobile")
    var cusMobileNumber: String? = "",
)
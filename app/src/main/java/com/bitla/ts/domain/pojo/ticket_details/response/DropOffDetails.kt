package com.bitla.ts.domain.pojo.ticket_details.response


import com.google.gson.annotations.SerializedName

data class DropOffDetails(
    @SerializedName("address")
    val address: String? = null,
    @SerializedName("arr_time")
    val arrTime: String? = "",
    @SerializedName("contact_numbers")
    val contactNumbers: String? = "",
    @SerializedName("contact_persons")
    val contactPersons: String? = "",
    @SerializedName("landmark")
    val landmark: String? = "",
    @SerializedName("stage_name")
    val stageName: String? = "",
    @SerializedName("latitude")
    val latitude: Any,
    @SerializedName("longitude")
    val longitude: Any,
    @SerializedName("pin_code")
    val pinCode: String? = "",
    @SerializedName("stage_id")
    val stageId: Any,
    @SerializedName("travel_date")
    val travelDate: String? = ""
)
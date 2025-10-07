package com.bitla.ts.domain.pojo.service_stages


import com.google.gson.annotations.SerializedName

data class StageDetailsItem(
    @SerializedName("act_as")
    val actAs: String?,
    @SerializedName("city")
    val city: String?,
    @SerializedName("city_id")
    val cityId: Int?,
    @SerializedName("contact_numbers")
    val contactNumbers: String?,
    @SerializedName("contact_persons")
    val contactPersons: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("is_next_day")
    val isNextDay: String?,
    @SerializedName("is_pick_up")
    val isPickUp: Boolean?,
    @SerializedName("latitude")
    val latitude: String?,
    @SerializedName("longitude")
    val longitude: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("passenger_details")
    val passengerDetails: List<PassengerDetail?>?,
    @SerializedName("pin_code")
    val pinCode: String?,
    @SerializedName("seq_number")
    val seqNumber: Int?,
    @SerializedName("state")
    val state: Int?,
    @SerializedName("time")
    val time: String?,
    @SerializedName("type")
    val type: Int?
)
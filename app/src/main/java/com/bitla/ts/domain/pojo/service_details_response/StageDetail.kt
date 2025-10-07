package com.bitla.ts.domain.pojo.service_details_response


import com.google.gson.annotations.SerializedName
import java.io.Serializable


class StageDetail : Serializable {

    @SerializedName("id")
    var id: Int? = null

    @SerializedName("name")
    var name: String? = null

    @SerializedName("type")
    var type: Int? = null

    @SerializedName("time")
    var time: String? = null

    @SerializedName("address")
    var address: String? = null

    @SerializedName("landmark")
    var landmark: String? = null

    @SerializedName("city_id")
    var cityId: Int? = null

    @SerializedName("city")
    var city: String? = null

    @SerializedName("state")
    var state: Int? = null

    @SerializedName("contact_numbers")
    var contactNumbers: String? = null

    @SerializedName("contact_persons")
    var contactPersons: String? = null

    @SerializedName("is_next_day")
    var isNextDay: String? = null

    @SerializedName("pin_code")
    var pinCode: String? = null

    @SerializedName("is_pick_up")
    var isPickUp: Boolean? = null

    @SerializedName("latitude")
    var latitude: Any? = null

    @SerializedName("longitude")
    var longitude: Any? = null

    @SerializedName("travel_date")
    var travelDate: String? = null

    var isChecked: Boolean = true
    val default_stage_id : String? = null

    @SerializedName("pickup_charge")
    val pickupCharge : String? = null

    @SerializedName("dropoff_charge")
    val dropoffCharge : String? = null

    @SerializedName("distance")
    val distance: String? = ""
}

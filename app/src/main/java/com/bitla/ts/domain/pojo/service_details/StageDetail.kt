package com.bitla.mba.morningstartravels.mst.pojo.service_details

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class StageDetail {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("type")
    @Expose
    var type: Int? = null

    @SerializedName("time")
    @Expose
    var time: String? = null

    @SerializedName("address")
    @Expose
    var address: String? = null

    @SerializedName("city_id")
    @Expose
    var cityId: Int? = null

    @SerializedName("city")
    @Expose
    var city: String? = null

    @SerializedName("state")
    @Expose
    var state: Int? = null

    @SerializedName("contact_numbers")
    @Expose
    var contactNumbers: String? = null

    @SerializedName("contact_persons")
    @Expose
    var contactPersons: String? = null

    @SerializedName("is_next_day")
    @Expose
    var isNextDay: String? = null

    @SerializedName("pin_code")
    @Expose
    var pinCode: String? = null

    @SerializedName("landmark")
    @Expose
    var landmark: String? = null

    @SerializedName("is_pick_up")
    @Expose
    var isPickUp: Boolean? = null

    @SerializedName("latitude")
    @Expose
    var latitude: String? = null

    @SerializedName("longitude")
    @Expose
    var longitude: String? = null

    @SerializedName("travel_date")
    @Expose
    var travelDate: String? = null
}
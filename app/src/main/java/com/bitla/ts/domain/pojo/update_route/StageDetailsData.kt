package com.bitla.ts.domain.pojo.update_route

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class StageDetailsData {
    @SerializedName("name")
    @Expose
    var name: String = ""

    @SerializedName("id")
    @Expose
    var id: String = ""

    @SerializedName("default_stage_id")
    @Expose
    var defaultStageId: String = ""

    @SerializedName("departure")
    @Expose
    var departure: String = ""

    @SerializedName("send_sms")
    @Expose
    var sendSms: Boolean = false

    @SerializedName("journey_day")
    @Expose
    var journeyDay: String = ""

    @SerializedName("is_pickup")
    @Expose
    var isPickup: Boolean = false

    @SerializedName("is_eticketing")
    @Expose
    var isEticketing: Boolean = true

    @SerializedName("is_api_booking")
    @Expose
    var isApiBooking: Boolean = true

    @SerializedName("contact_no")
    @Expose
    var contactNo: String = ""

    @SerializedName("person")
    @Expose
    var person: String = ""

    @SerializedName("address_1")
    @Expose
    var address1: String = ""

    @SerializedName("address_line1")
    @Expose
    var addressLine1: String = ""

    @SerializedName("address_line2")
    @Expose
    var addressLine2: String = ""

    @SerializedName("address_2")
    @Expose
    var address2: String = ""

    @SerializedName("landmark")
    @Expose
    var landmark: String = ""

    @SerializedName("location_url")
    @Expose
    var locationUrl: String = ""

    @SerializedName("latitude")
    @Expose
    var lat: String = ""

    @SerializedName("longitude")
    @Expose
    var long: String = ""

    @SerializedName("departure_hour")
    @Expose
    var departureHour: String = ""

    @SerializedName("departure_mins")
    @Expose
    var departureMinute: String = ""
}
package com.example.buscoach.service_details_response


import com.google.gson.annotations.SerializedName
import java.io.Serializable


class ServiceDetailsModel: Serializable {

    @SerializedName("code")
    var code: Int? = null

    @SerializedName("success")
    var success: Boolean? = null

    @SerializedName("body")
    var body: Body? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("show_fare_seat")
    var showFareOnSeat: Boolean = false

    @SerializedName("set_max_seat_selection")
    var setMaxSeatSelection: Int = 0

    @SerializedName("currency")
    var currency: String = "Rs."

}

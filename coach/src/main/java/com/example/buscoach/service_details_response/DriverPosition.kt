package com.example.buscoach.service_details_response

import com.google.gson.annotations.SerializedName

class DriverPosition {

    @SerializedName("Row")
    var row: Int = 0

    @SerializedName("Col")
    var col: Int? = null

    @SerializedName("Position")
    var position: String? = null

    @SerializedName("Maxcolcount")
    var maxcolcount: String? = null

    @SerializedName("Mincolcount")
    var mincolcount: String? = null

    @SerializedName("IsDriverExistInSeat")
    var isDriverExistInSeat: Boolean = false

}
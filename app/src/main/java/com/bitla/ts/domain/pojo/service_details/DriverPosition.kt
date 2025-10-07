package com.bitla.mba.morningstartravels.mst.pojo.service_details

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DriverPosition {

    @SerializedName("Row")
    @Expose
    var row: Int = 0

    @SerializedName("Col")
    @Expose
    var col: Int? = null

    @SerializedName("Position")
    @Expose
    var position: String? = null

    @SerializedName("Maxcolcount")
    @Expose
    var maxcolcount: String? = null

    @SerializedName("Mincolcount")
    @Expose
    var mincolcount: String? = null

    @SerializedName("IsDriverExistInSeat")
    @Expose
    var isDriverExistInSeat: Boolean = false

}
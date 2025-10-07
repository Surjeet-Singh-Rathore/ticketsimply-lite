package com.bitla.mba.morningstartravels.mst.pojo.booking

import com.bitla.ts.domain.pojo.booking.Body
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BookingModel {

    @SerializedName("code")
    @Expose
    var code: Int? = null

    @SerializedName("success")
    @Expose
    var success: Boolean? = null

    @SerializedName("body")
    @Expose
    var body: Body? = null

}

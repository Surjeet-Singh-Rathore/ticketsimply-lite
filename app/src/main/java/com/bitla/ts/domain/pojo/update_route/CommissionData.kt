package com.bitla.ts.domain.pojo.update_route

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CommissionData {


    @SerializedName("commission_type")
    @Expose
    var commisionType: String = ""

    @SerializedName("commission_value")
    @Expose
    var commissionValue: String = ""

    @SerializedName("seat_type")
    @Expose
    var seatType: String = ""

}
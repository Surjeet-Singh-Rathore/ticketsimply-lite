package com.bitla.ts.domain.pojo.coach_type

import com.bitla.ts.domain.pojo.route_list.RouteListData
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CoachTypeListData {

    @SerializedName("id")
    @Expose
    var id: String = ""

    @SerializedName("label")
    @Expose
    var label: String = ""

    @SerializedName("total_seats")
    @Expose
    var totalSeats: String = ""
}
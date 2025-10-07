package com.bitla.ts.domain.pojo.coach_type

import com.bitla.ts.domain.pojo.route_list.RouteListData
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CoachTypeListResponse {

    @SerializedName("code")
    @Expose
    var code: Int = 0

    @SerializedName("result")
    @Expose
    var result: CoachResult?= null
}
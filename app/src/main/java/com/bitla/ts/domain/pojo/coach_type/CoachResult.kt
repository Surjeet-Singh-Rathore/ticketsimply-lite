package com.bitla.ts.domain.pojo.coach_type

import com.bitla.ts.domain.pojo.route_list.RouteListData
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CoachResult {

    @SerializedName("coach_types")
    @Expose
    var coachTypes: ArrayList<CoachTypeListData> = arrayListOf()
}
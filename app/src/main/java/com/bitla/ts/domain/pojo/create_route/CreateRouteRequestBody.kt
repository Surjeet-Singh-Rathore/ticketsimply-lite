package com.bitla.ts.domain.pojo.create_route

import com.bitla.ts.domain.pojo.stage_for_city.StageListData
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CreateRouteRequestBody {

    @SerializedName("basic_details")
    @Expose
    var basicDetails: ArrayList<BasicDetailsData> = arrayListOf()

    @SerializedName("schedule")
    @Expose
    var schedule: ArrayList<ScheduleData> = arrayListOf()

    @SerializedName("other")
    @Expose
    var other: ArrayList<OtherData> = arrayListOf()
}
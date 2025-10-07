package com.bitla.ts.domain.pojo.stage_summary_details

import com.google.gson.annotations.SerializedName

class BoardingStageDetails {
    @SerializedName("boarding_point_name") var boarding_point_name: String? = null
    @SerializedName("total_seats") var total_seats   : ArrayList<String> = arrayListOf<String>()
}
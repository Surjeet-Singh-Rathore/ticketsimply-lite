package com.bitla.ts.domain.pojo.stage_summary_details

import com.google.gson.annotations.SerializedName

class StageSummaryModel {
    @SerializedName("code"    ) var code    : Int?    = null
    @SerializedName("message" ) var message : String? = null
    @SerializedName("result"  ) var result  : Result? = Result()
}
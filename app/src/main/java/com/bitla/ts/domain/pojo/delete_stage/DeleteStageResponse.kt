package com.bitla.ts.domain.pojo.delete_stage

import com.bitla.ts.domain.pojo.update_route.ResultUpdateRoute
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DeleteStageResponse {

    @SerializedName("code")
    @Expose
    val code: Int = 0

    @SerializedName("result")
    @Expose
    val result: ResultUpdateRoute ?= null
}
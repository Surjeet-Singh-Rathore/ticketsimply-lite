package com.bitla.ts.domain.pojo.dashboard_model.response


import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("message")
    val message: String?
)
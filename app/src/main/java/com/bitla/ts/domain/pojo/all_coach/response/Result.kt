package com.bitla.ts.domain.pojo.all_coach.response

import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("message")
    val message: String?
)
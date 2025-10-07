package com.bitla.ts.domain.pojo.ticket_details.response


import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("message")
    val message: String?
)
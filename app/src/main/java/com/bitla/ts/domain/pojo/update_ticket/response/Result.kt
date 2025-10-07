package com.bitla.ts.domain.pojo.update_ticket.response


import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("message")
    val message: String
)
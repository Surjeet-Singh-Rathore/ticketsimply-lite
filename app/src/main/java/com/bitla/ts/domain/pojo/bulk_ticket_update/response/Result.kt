package com.bitla.ts.domain.pojo.bulk_ticket_update.response


import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("message")
    val message: String?
)
package com.bitla.ts.domain.pojo.book_ticket.release_ticket.response


import com.google.gson.annotations.SerializedName

data class ReleaseAgentRechargBlockedSeatsResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String,
)
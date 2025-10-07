package com.bitla.ts.domain.pojo.ticket_details.response


import com.google.gson.annotations.SerializedName

data class ServiceRequests(
    @SerializedName("panic")
    val panic: List<Any>,
    @SerializedName("toilet_break")
    val toiletBreak: List<String>
)
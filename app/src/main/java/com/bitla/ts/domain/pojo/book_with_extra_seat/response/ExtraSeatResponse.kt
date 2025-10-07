package com.bitla.ts.domain.pojo.book_with_extra_seat.response


import com.google.gson.annotations.SerializedName

data class ExtraSeatResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String
)
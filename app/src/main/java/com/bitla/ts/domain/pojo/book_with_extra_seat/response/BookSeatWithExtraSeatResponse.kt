package com.bitla.ts.domain.pojo.book_with_extra_seat.response

import com.bitla.ts.domain.pojo.book_ticket_full.Result
import com.google.gson.annotations.SerializedName

data class BookSeatWithExtraSeatResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("body")
    val result: Result,
    @SerializedName("message")
    val message: String?
)
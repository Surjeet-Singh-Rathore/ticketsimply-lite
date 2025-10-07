package com.bitla.ts.domain.pojo.book_ticket.release_ticket.response


import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("message")
    val message: String
)
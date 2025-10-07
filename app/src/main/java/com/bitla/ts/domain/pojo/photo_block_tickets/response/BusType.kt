package com.bitla.ts.domain.pojo.photo_block_tickets.response


import com.google.gson.annotations.SerializedName

data class BusType(
    @SerializedName("html_safe")
    val htmlSafe: Boolean,
    @SerializedName("^o")
    val o: String,
    @SerializedName("self")
    val self: String
)
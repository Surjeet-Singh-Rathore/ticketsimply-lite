package com.bitla.ts.domain.pojo.photo_block_tickets.response


import com.google.gson.annotations.SerializedName

data class BoardingDetails(
    @SerializedName("address")
    val address: String,
    @SerializedName("contact_numbers")
    val contactNumbers: String,
    @SerializedName("dep_time")
    val depTime: String,
    @SerializedName("landmark")
    val landmark: String,
    @SerializedName("stage_name")
    val stageName: String
)
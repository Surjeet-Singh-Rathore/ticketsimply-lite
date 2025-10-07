package com.bitla.ts.domain.pojo.luggage_details.response

import com.google.gson.annotations.SerializedName

data class FetchLuggageDetailsResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("luggage_description")
    val luggageDesc: String
)
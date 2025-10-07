package com.bitla.ts.domain.pojo.luggage_details.response

import com.google.gson.annotations.SerializedName

data class LuggageOptionsDetailsResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String
)

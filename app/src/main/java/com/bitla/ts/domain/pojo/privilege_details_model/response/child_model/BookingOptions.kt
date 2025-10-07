package com.bitla.ts.domain.pojo.privilege_details_model.response.child_model


import com.google.gson.annotations.SerializedName

data class BookingOptions(
    @SerializedName("Onl-Agt")
    val onlAgt: OnlAgt,
    @SerializedName("Walkin")
    val walkin: List<Any>
)
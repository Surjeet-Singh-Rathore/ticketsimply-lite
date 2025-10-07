package com.bitla.ts.domain.pojo.my_bookings.response


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Filter(
    @SerializedName("id")
    @Expose
    var id: Any? = null,
    @SerializedName("label")
    var label: String? = null
)
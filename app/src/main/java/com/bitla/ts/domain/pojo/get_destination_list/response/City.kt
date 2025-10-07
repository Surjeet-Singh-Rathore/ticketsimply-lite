package com.bitla.ts.domain.pojo.get_destination_list.response


import com.google.gson.annotations.SerializedName

data class City(
    @SerializedName("city")
    val city: CityX?,
    @SerializedName("dropping_point")
    val droppingPoint: List<DroppingPoint>?
)
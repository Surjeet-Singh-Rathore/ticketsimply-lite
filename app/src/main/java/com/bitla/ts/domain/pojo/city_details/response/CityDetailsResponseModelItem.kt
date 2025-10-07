package com.bitla.ts.domain.pojo.city_details.response


import com.google.gson.annotations.SerializedName

data class CityDetailsResponseModelItem(
    @SerializedName("id")
    var id: Int,
    @SerializedName("name")
    var name: String
)
package com.bitla.ts.domain.pojo.state_details.response

import com.google.gson.annotations.SerializedName

data class StateDetailsResponseModel(
    @SerializedName("code")
    val code: Int,
    @SerializedName("states")
    val states: List<States>
)
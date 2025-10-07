package com.bitla.ts.domain.pojo.campaigns_and_promotions_discount.request

import com.google.gson.annotations.SerializedName

data class ReqBody(
    @SerializedName("agent_type")
    val agentType: Int?,
    @SerializedName("selected_seat_no")
    val selectedSeatNo: List<SelectedSeatNo?>?
)
package com.bitla.ts.domain.pojo.crew_update.request

data class UpdateStuffGoodsDetail(
    val id: Int,
    val is_checked: Boolean?,
    val name: String,
    val remarks: String
)
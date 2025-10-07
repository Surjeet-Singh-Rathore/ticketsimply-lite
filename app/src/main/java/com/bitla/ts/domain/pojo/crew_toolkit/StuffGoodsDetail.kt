package com.bitla.ts.domain.pojo.crew_toolkit

data class StuffGoodsDetail(
    val id: Int,
    var is_checked: Boolean?,
    val name: String,
    var remarks: String,
    var stuff_goods_image_details: MutableList<StuffImage>,
    var uriImages: MutableList<String>,
)
package com.bitla.ts.domain.pojo.crew_toolkit

data class CrewToolKIt(
    val code: Int,
    val pdf_link: String,
    val stuff_goods_details: MutableList<StuffGoodsDetail>,
    var result: Result,
    val message: String
)
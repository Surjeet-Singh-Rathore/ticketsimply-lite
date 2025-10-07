package com.bitla.ts.domain.pojo.city_pickup_by_chart_stage.response

data class CityPickupChartByStageResponse(
    val closed_on: String,
    val code: Int,
    val header: String,
    val status: Int,
    val result: Result
)

data class Result(
    val message: String
)
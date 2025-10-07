package com.bitla.ts.domain.pojo.city_pickup_by_chart_stage.request

data class CityPickupChartByStageRequest(
    val bcc_id: String,
    val format: String,
    val method_name: String,
    val req_body: ReqBody
)
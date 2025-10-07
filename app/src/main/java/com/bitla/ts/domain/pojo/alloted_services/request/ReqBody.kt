package com.bitla.ts.domain.pojo.alloted_services.request

data class ReqBody(
    val api_key: String,
    val travel_date: String,
    val origin: String?,
    val destination: String?,
    val is_group_by_hubs: Boolean,
    val is_from_middle_tier: Boolean,
    var locale: String?,
    var view_mode: String? = null,
    var hub_id: String? = null

)
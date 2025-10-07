package com.bitla.ts.domain.pojo.merge_service_details.request

data class MergeServiceDetailsRequest(
    val apiKey: String?,
    val resId: String?,
    val originId: String?,
    val destinationId: String?,
    val excludePassengerDetails: Boolean?,
    val locale: String?
)
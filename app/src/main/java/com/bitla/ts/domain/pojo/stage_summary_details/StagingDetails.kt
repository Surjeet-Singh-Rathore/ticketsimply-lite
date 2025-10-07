package com.bitla.ts.domain.pojo.stage_summary_details

data class StagingDetails(
    var bus_stop: String? = String(),
    var seats: ArrayList<String> = arrayListOf<String>()
)

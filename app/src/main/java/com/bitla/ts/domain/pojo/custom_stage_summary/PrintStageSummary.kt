package com.bitla.ts.domain.pojo.custom_stage_summary

data class PrintStageSummary(
    val boarding_dropping: String,
    val count: Int,
    var isBoarding: Boolean = false
)
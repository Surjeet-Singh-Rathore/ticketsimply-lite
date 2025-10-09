package com.bitla.ts.phase2.chartUtils

data class ReportValue(
    val name: String,
    val value: String,
    val color: Int,
    val chatFullName: String? = null,
    val chartFullValue: String? = null,
)
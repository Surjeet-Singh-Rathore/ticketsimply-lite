package com.bitla.ts.phase2.dashboard_pojo

data class PerformanceSummaryModel(
    var source: String,
    var seatsSold: String,
    var revenueNet: Any,
    var revenueGross: Any? = null,
)

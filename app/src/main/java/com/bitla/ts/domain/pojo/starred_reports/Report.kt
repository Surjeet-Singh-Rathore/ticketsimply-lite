package com.bitla.ts.domain.pojo.starred_reports

data class Report(
    val generated_date: String,
    val pdf_url: String,
    val rep_from_date: String? = "",
    val rep_to_date: String? = "",
    val report_name: String,
    val service_info: String?
)
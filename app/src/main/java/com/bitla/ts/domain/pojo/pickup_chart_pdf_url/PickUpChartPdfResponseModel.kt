package com.bitla.ts.domain.pojo.pickup_chart_pdf_url

data class PickUpChartPdfResponseModel(
    val pdf_url: String,
    val status: Int,
    val result: Message?
)


class Message(
    val message: String?
)

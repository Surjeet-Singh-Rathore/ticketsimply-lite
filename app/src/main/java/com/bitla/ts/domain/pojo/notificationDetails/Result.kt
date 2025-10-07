package com.bitla.ts.domain.pojo.notificationDetails

data class Result(
    val `data`: List<Data>,
    val footer_message: String,
    val upper_block_label: String,
    val upper_block_short_desc: String
)
package com.bitla.ts.domain.pojo.ticket_details.response

data class InsuranceDetails(
    val details: List<Detail>?,
    val partner_trans_id: String
)
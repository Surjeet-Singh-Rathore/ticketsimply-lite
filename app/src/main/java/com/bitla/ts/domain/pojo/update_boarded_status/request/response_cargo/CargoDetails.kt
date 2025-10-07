package com.bitla.ts.domain.pojo.update_boarded_status.request.response_cargo

data class CargoDetails(
    val amount: String,
    val item: String,
    val item_type: Int,
    val quantity: Int,
    val status_is_already_updated: String,
    val tag_no: Int
)
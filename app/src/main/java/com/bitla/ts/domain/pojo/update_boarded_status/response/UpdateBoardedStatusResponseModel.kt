package com.bitla.ts.domain.pojo.update_boarded_status.response

import com.bitla.ts.domain.pojo.update_boarded_status.CargoDetails

data class UpdateBoardedStatusResponseModel(
    val cargo_details: CargoDetails,
    val code: Int,
    val header: String,
    val message: String?=null,
    val seat_number: String,
    val status: String,
    val result: Message
)

class Message(
    val message: String?=null
)
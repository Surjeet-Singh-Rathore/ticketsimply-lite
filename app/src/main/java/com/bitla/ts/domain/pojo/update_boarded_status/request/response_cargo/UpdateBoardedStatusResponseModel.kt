package com.bitla.ts.domain.pojo.update_boarded_status.request.response_cargo

data class UpdateBoardedStatusResponseModel(
    val cargo_details: CargoDetails,
    val code: Int,
    val header: String,
    val message: String,
    val result: Result
)

class Result(
    val message: String
)
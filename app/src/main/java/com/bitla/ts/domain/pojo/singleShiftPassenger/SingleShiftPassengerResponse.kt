package com.bitla.ts.domain.pojo.singleShiftPassenger

data class SingleShiftPassengerResponse(
    val code: Int,
    val message: String,
    val result: Result?
)

class Result(
    val message: String?
)
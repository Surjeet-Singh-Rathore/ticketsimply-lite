package com.bitla.ts.domain.pojo.passenger_history

data class PassengersHistory(
    val body: MutableList<PassengerHistoryModel>,
    val code: Int? = null,
    val success: Boolean,
    val message: String
)
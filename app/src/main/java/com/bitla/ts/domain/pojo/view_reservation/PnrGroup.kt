package com.bitla.ts.domain.pojo.view_reservation

data class PnrGroup(
    val passenger_details: ArrayList<PassengerDetailX?>?,
    val passenger_details1: ArrayList<PassengerDetail?>?,
    val pnr_number: String?,
    val boarded: String?,
    val yet_to_board: String?,
    val booked: String?,
    val remarks: String?,
    val booked_by: String?,
    val total_ticket_fare: String?,
    val booked_by_operator: String?,
    val is_meal: Boolean?,
    val is_pay_at_bus: Boolean?,
    val is_partial_payment: Boolean?,
    val total_discount: String?,
    val total_net_fare: String?,
    val pickup_address: String?,
    val dropoff_address: String?,
    val pnr_status: String?,
    val seat_number_group: ArrayList<String>?,
    )
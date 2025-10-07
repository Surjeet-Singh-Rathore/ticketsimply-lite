package com.bitla.ts.domain.pojo.rutDiscountDetails.request

class RutDiscountRequest (
    val seat_number: String,
    val reservation_id: String,
    val origin: String,
    val destination: String,
    val rut_number: String,
    val no_of_seats: Int,
    val date: String,
    val api_key: String,
    var locale:String?,
    var is_from_middle_tier:Boolean
)
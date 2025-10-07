package com.bitla.ts.domain.pojo.getPrefillPassenger


data class GetPrefillPassengerRequest (
    val card_number: String,
    val card_type: Int,
    val seat_number: String,
    val api_key: String,
    val locale: String?,
    val is_from_middle_tier: Boolean
        )

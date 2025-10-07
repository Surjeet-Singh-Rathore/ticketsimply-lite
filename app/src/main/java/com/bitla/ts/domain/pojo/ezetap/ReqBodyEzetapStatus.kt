package com.bitla.ts.domain.pojo.ezetap


import com.google.gson.JsonObject
import org.json.JSONObject

data class ReqBodyEzetapStatus(
    val api_key: String,
    val pnr_number: String,
    val is_send_sms: Boolean,
    val is_extra_seat: Boolean = false,
    val reservation_id: String = "",
    val ezetap_response: JsonObject ?= null,
    val ezetap_payment_type: Int ?= 0,

)


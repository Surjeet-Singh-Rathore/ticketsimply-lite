package com.bitla.ts.domain.pojo.pinelabs

import com.google.gson.annotations.SerializedName
import org.json.JSONObject

data class ReqBodyPinelab(
    val api_key: String,
    val pnr_number: String,
    val is_send_sms: Boolean,
    val is_extra_seat: Boolean = false,
    val reservation_id: String = "",
    val origin_id: String = "",
    val destination_id: String = "",
    val pinelab_response: JSONObject ?= null,
    val pinelab_payment_type: Int ?= 0,

)


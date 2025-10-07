package com.bitla.ts.domain.pojo.sms_types

data class SmsTemplate(
    val sms_content: String,
    val sms_id: Any,
    val sms_input_mode: List<SmsInputMode>,
    val sms_type: String
)
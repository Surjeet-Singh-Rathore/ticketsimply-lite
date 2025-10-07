package com.bitla.ts.domain.pojo.sms_types

data class SmsInputMode(
    val default_option: String,
    val is_employee_option: Boolean,
    val is_input_field: Boolean,
    val is_modify_template: Boolean,
    val name: String,
    val options: List<String>
)
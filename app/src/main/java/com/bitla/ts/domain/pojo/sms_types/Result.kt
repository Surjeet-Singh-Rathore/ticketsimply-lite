package com.bitla.ts.domain.pojo.sms_types

data class Result(
    val employee_type_options: MutableList<EmployeeTypeOption>,
    val sms_templates: List<SmsTemplate>,
    val message: String?,

    )

data class EmployeeTypes(var employee_type_options: MutableList<EmployeeTypeOption>)
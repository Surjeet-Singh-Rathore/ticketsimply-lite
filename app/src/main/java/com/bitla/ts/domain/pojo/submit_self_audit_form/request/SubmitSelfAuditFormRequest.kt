package com.bitla.ts.domain.pojo.submit_self_audit_form.request

data class SubmitSelfAuditFormRequest(
    val api_key: String,
    val bp_questions: List<BpQuestion>,
    val normal_questions: List<NormalQuestion>,
    val rating: Rating?,
    val remarks: String,
    val res_id: String
)
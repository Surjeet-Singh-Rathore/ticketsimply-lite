package com.bitla.ts.domain.pojo.submit_self_audit_form.request

data class BpQuestion(
    val answer_list: List<Answer>?,
    val question_id: String?
)
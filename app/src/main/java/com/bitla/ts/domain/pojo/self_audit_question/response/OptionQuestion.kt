package com.bitla.ts.domain.pojo.self_audit_question.response

data class OptionQuestion(
    val options: List<Option>,
    val question: String,
    val question_id: String,
    val type: String
)
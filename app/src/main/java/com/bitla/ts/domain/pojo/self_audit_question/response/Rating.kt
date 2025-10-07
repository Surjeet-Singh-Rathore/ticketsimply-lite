package com.bitla.ts.domain.pojo.self_audit_question.response

data class Rating(
    val id: String,
    val rating_options: List<RatingOption>,
    val rating_title: String
)
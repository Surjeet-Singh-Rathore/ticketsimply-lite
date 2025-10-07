package com.bitla.ts.domain.pojo.self_audit_question.response

data class Result(
    val boarding_data: List<BoardingData>,
    val date: String,
    val email: String,
    val operator_name: String,
    val option_questions: List<OptionQuestion>,
    val ratings: Rating,
    val route_name: String,
    val trip_id: String
)
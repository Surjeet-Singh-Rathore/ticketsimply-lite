package com.bitla.ts.domain.pojo.self_audit_question.response

data class BoardingData(
    val boarding_points: List<BoardingPoint>,
    val city_name: String
)
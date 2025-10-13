package com.bitla.ts.domain.pojo.get_coach_details.response


import com.google.gson.annotations.SerializedName

data class CoachDetailsResponseItem(
    @SerializedName("coach_id")
    val coachId: Int?,
    @SerializedName("coach_number")
    val coachNumber: String?
)
package com.bitla.ts.domain.pojo.announcement_details_model.response


import com.google.gson.annotations.SerializedName

data class ResultX(
    @SerializedName("announcement_message")
    val announcementMessage: List<AnnouncementMessage>,
    @SerializedName("beep_sound_url")
    val beepSoundUrl: String
)
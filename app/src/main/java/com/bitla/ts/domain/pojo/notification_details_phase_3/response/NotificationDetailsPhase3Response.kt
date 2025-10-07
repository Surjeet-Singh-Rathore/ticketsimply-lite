package com.bitla.ts.domain.pojo.notification_details_phase_3.response


import com.google.gson.annotations.SerializedName

data class NotificationDetailsPhase3Response(
    @SerializedName("code")
    val code: Int,
    @SerializedName("is_grid")
    val isGrid: Boolean,
    @SerializedName("is_msg_having_feedback")
    val isMsgHavingFeedback: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("notification_msg")
    val notificationMsg: String,
    @SerializedName("notification_tag")
    val notificationTag: String,
    @SerializedName("result")
    val result: List<Result>,
    @SerializedName("upper_block_label")
    val upperBlockLabel: String,
    @SerializedName("upper_block_short_desc")
    val upperBlockShortDesc: String
)
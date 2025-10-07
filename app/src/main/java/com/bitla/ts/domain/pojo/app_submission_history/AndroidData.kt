package com.bitla.ts.domain.pojo.app_submission_history

import com.google.gson.annotations.SerializedName

data class AndroidData(
    @SerializedName("version_number")
    val versionNumber: String= "",
    @SerializedName("is_critical_update")
    val isCriticalUpdate: Boolean = false,
)

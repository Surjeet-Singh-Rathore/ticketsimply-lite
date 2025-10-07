package com.bitla.ts.domain.pojo.app_submission_history

import com.google.gson.annotations.SerializedName

data class AppSubmissionHistory(
    @SerializedName("Android")
    val android: AndroidData = AndroidData()
)

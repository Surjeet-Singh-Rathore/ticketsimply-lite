package com.bitla.ts.domain.pojo.bulk_cancellation

import com.bitla.ts.domain.pojo.cancel_partial_ticket_model.response.Result
import com.google.gson.annotations.SerializedName

data class BulkCancellationResponseModel(
    val code: String,
    val message: String?,
    @SerializedName("result")
    val result: Result?
)
data class Result(
    @SerializedName("key")
    val key: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("otp_validation")
    val otpValidation: Boolean
)
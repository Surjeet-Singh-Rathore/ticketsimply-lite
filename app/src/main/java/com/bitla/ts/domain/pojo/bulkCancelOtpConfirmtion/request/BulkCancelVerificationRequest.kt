package com.bitla.ts.domain.pojo.bulkCancelOtpConfirmtion.request

data class BulkCancelVerificationRequest(
    val api_key: String,
    val bulk_cancel_params: List<com.bitla.ts.domain.pojo.bulk_cancellation.request.BulkCancelParam>,
    val cancel_percent: Float,
    val is_from_middle_tier: Boolean,
    val is_sms_send: Boolean,
    val key: String,
    val locale: String,
    val otp: String,
    val remarks: String,
    val res_id: String
)
package com.bitla.ts.domain.repository

import com.bitla.ts.data.ApiInterface
import com.bitla.ts.domain.pojo.sms_types.request.SmsTypesRequest
import com.bitla.ts.domain.pojo.submit_self_audit_form.request.SubmitSelfAuditFormRequest
import com.bitla.ts.koin.models.makeApiCall

class SelfAuditRepository(private val apiInterface: ApiInterface) {

    suspend fun getSelfAuditQuestions(
        apiKey: String,
        resId: String

    ) = apiInterface.getCheckListQuestions(
        apiKey,resId
    )
    suspend fun selfAuditFormSubmit(
        selfAuditFormSubmitRequest: SubmitSelfAuditFormRequest

    ) = apiInterface.selfAuditFormSubmitApi(
        selfAuditFormSubmitRequest
    )
}

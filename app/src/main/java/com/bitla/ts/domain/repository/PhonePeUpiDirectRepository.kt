package com.bitla.ts.domain.repository

import com.bitla.ts.data.ApiInterface
import com.bitla.ts.domain.pojo.phonepe_direct_upi_for_app.request.PhonePeDirectUPIForAppRequest
import com.bitla.ts.domain.pojo.phonepe_direct_upi_transaction_status.request.PhonePeDirectUPITransactionStatusRequest
import com.bitla.ts.koin.models.makeApiCall

class PhonePeUpiDirectRepository(private val apiInterface: ApiInterface) {

    suspend fun getPhonePeDirectValidateUpiId(
        apiKey: String?,
        vpa: String?,
    ) = makeApiCall { apiInterface.getPhonePeDirectValidateUpiId(apiKey,vpa) }

    suspend fun getPhonePeDirectTransactionStatus(
        phonePeDirectUPITransactionStatusRequest: PhonePeDirectUPITransactionStatusRequest,
    ) = makeApiCall { apiInterface.getPhonePeDirectTransactionStatus(phonePeDirectUPITransactionStatusRequest) }

    suspend fun getPhonePeDirectUpiForApp(
        apiKey: String?,
        phonePeDirectUPIForAppRequest: PhonePeDirectUPIForAppRequest,
    ) = makeApiCall { apiInterface.getPhonePeDirectUPIForApp(apiKey, phonePeDirectUPIForAppRequest) }

}
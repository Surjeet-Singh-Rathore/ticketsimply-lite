package com.bitla.ts.domain.repository

import com.bitla.ts.data.ApiInterface
import com.bitla.ts.domain.pojo.agent_recharge.request.*
import com.bitla.ts.koin.models.makeApiCall
import com.bitla.ts.domain.pojo.instant_recharge.AgentPGDataResponse
import okhttp3.RequestBody
import retrofit2.Response


class BranchRechargeRepository(private val apiInterface: ApiInterface) {
    
    suspend fun newBranchRecharge(
        branchRechargeRequest: ReqBody,
    ) = makeApiCall { apiInterface.newBranchRechargeApi(branchRechargeRequest) }
    
    
    suspend fun newConfirmBranchRecharge(
        branchRechargeRequest: ConfirmAgentRequestBody,
    ) = makeApiCall { apiInterface.newConfirmBranchRechargeApi(branchRechargeRequest) }
    
    
    suspend fun newAgentRecharge(
        agentRechargeRequest: AgentReqBody,
    ) = makeApiCall { apiInterface.agentRechargeApi(agentRechargeRequest) }
    
    
    suspend fun newConfirmAgentRecharge(
        confirmAgentRechargeRequest: ConfirmAgentRequestBody,
    ) = makeApiCall { apiInterface.newConfirmAgentRechargeApi(confirmAgentRechargeRequest) }
    
    
    suspend fun getAgentTransactionDetailApi(
        apiKey: String,
        amount: String,
        locale: String,
    ) = makeApiCall { apiInterface.getAgentTransactionDetailsApi(apiKey, amount, locale) }
    
    suspend fun getAgentPGDetailApi(
        apiKey: String,
        amount: String,
        pgType: String,
        nativeAppType: Int,
    ) = makeApiCall { apiInterface.getAgentPGDetail(apiKey, amount, pgType, nativeAppType) }
    
    suspend fun getPhonePeStatusApi(
        apiKey: String,
        pnrNumber: String,
        
        ) = makeApiCall { apiInterface.getPhonePeTransStatus(pnrNumber, api_key = apiKey) }
    
    
    suspend fun getRazorPaySuccess(
        pnrNumber: String,
        paymentId: String,
    ) = makeApiCall { apiInterface.getRazorPaySuccess(true, pnrNumber, paymentId) }
    
    suspend fun getEasebuzzSuccess(
        isEaseBuzzPayment: Boolean,
        pnrNumber: String,
        amount: String,
        phoneNo: String,
        emailId: String,
    ) = makeApiCall {
        apiInterface.getEaseBuzzSuccess(
            isEaseBuzzPayment = isEaseBuzzPayment,
            pnrNum = pnrNumber,
            amount = amount,
            phone = phoneNo,
            email = emailId
        )
    }
    
    suspend fun getPayBitlaSuccess(
        pnrNumber: String,
    ) = apiInterface.getPayBitlaTransStatus(pnrNumber)
    
    suspend fun getRazorPayFailure(
        orderId: String,
        pnrNumber: String,
    ) = makeApiCall { apiInterface.getRazorPayFailure(
        orderId = orderId,
        isRazorpayPayment = true,
        pnrNum = pnrNumber
    ) }
    
    suspend fun getPhonepePayPageTransactionStatus(
        xVerify: String,
        body: RequestBody,
        
        ) = makeApiCall { apiInterface.getPhonepePayPageTransactionStatus(xVerify, body) }
    
    suspend fun getPhonePeV2Status(
        apiKey: String,
        orderId: String
    ) = makeApiCall { apiInterface.getPhonePeV2Status(apiKey, orderId) }

    suspend fun phonePeV2RechargeSuccessConPay(
        pnrNumber: String
    ) = makeApiCall { apiInterface.phonePeV2RechargeSuccessConPay(true, pnrNumber) }
}

package com.bitla.ts.domain.repository

import com.bitla.ts.data.*
import com.bitla.ts.domain.pojo.book_ticket.release_ticket.request.*
import com.bitla.ts.domain.pojo.dashboard_model.release_ticket.request.*
import com.bitla.ts.domain.pojo.login_auth_post.request.LoginAuthPostRequest
import com.bitla.ts.domain.pojo.logout_auth_post_req.FullLogoutReqBody
import com.bitla.ts.domain.pojo.logout_auth_post_req.LogoutReqBody
import com.bitla.ts.koin.models.makeApiCall
import com.bitla.ts.utils.security.EncrypDecryp

class DashboardRepository(private val apiInterface: ApiInterface) {
    suspend fun initDomain() = makeApiCall {
        apiInterface.initDomain()
    }
//    suspend fun getNewLoginDetails(login: String, pass: String, locale:String?, deviceId: String) = makeApiCall {  apiInterface.newLoginApi(EncrypDecryp.getEncryptedValue(login), EncrypDecryp.getEncryptedValue(pass),locale, EncrypDecryp.getEncryptedValue(deviceId), is_encrypted = EncrypDecryp.isEncrypted())}
    suspend fun getNewLoginDetails(login: String, pass: String, locale:String?, deviceId: String, shiftId: Int? = null, counterId: Int?= null, counterBalance: String = "") = makeApiCall {   apiInterface.newLoginApi(login, pass,locale, deviceId, shiftId, counterId, counterBalance)}

    suspend fun getNewLoginDetailsPost(loginAuthPostRequest: LoginAuthPostRequest) = makeApiCall {  apiInterface.newLoginApiPost(loginAuthPostRequest)}

    suspend fun getNewResetDetails(username: String, pass: String, deviceId: String, shiftId: Int? = null, counterId: Int?= null, counterBalance: Double? = null) = makeApiCall {  apiInterface.newResetApi(username, pass, deviceId, shiftId, counterId, counterBalance) }
    suspend fun getNewResetPostApi(logoutReqBody: LogoutReqBody) = makeApiCall {
        apiInterface.logoutPostApi(logoutReqBody)
    }

    suspend fun newFullLogout(apiKey: String,deviceId: String,closeCounter: Boolean) = makeApiCall {  apiInterface.newFullLogoutApi(apiKey, true, deviceId,closeCounter) }
    suspend fun newFullPostLogout(logoutFullReqBody: FullLogoutReqBody,closeCounter: Boolean) = makeApiCall {  apiInterface.newFullLogoutPostApi(logoutFullReqBody,closeCounter) }


    suspend fun newGetLoginWithOTPDetails(loginWithOtpRequest: com.bitla.ts.domain.pojo.login_with_otp.request.ReqBody) = makeApiCall {  apiInterface.newLoginWithOTPApi(loginWithOtpRequest) }

    suspend fun newGetDashboardData(
        reqBody: com.bitla.ts.domain.pojo.dashboard_model.request.ReqBody
    ) = makeApiCall { apiInterface.newDashboardSummaryFragment(reqBody.api_key,reqBody.json_format,reqBody.locale?:"") }



    suspend fun newGetReleaseTicketData(
        releaseTicketRequest: ReqBody
    ) = makeApiCall { apiInterface.newGetReleaseTicketApi(releaseTicketRequest) }
   
    suspend fun newGetReleaseBimaTicketData(
        releaseTicketRequest: ReqBody
    ) = makeApiCall { apiInterface.newGetReleaseBimaTicketApi(releaseTicketRequest) }


    suspend fun newGetReleaseTicketApiWithoutTicket(
        releaseTicketRequest: ReqBodyWithoutTicket
    ) = makeApiCall { apiInterface.newGetReleaseTicketApiWithoutTicket(releaseTicketRequest) }
    
    suspend fun newGetReleaseAgentRechargBlockedSeatsResponse(
        releaseTicketRequest: ReleaseAgentRechargBlockedSeatsRequest
    ) = makeApiCall { apiInterface.newGetReleaseAgentRechargBlockedSeatsResponse(releaseTicketRequest) }

    suspend fun releaseBranchUpiBlockedSeatsApi(
        releaseTicketRequest: ReleaseAgentRechargBlockedSeatsRequest
    ) = makeApiCall { apiInterface.releaseBranchUpiBlockedSeatsApi(releaseTicketRequest) }


    suspend fun newOccupancyCalendar(
        reqBody: com.bitla.ts.phase2.dashboard_pojo.occupancyCalendarModel.request.ReqBody
    ) = makeApiCall {
        apiInterface.newOccupancyCalendar(
            reqBody.apiKey,
            reqBody.endDate,
            reqBody.reservationId.toString(),
            reqBody.startDate
        )
    }


    suspend fun newStoreFcmKey(
        apiKey: String,
        device_id: String,
        fcm_key: String
    ) = makeApiCall { apiInterface.newStoreFcmKey(apiKey, device_id, fcm_key) }


    suspend fun fetchCreditInfo(
        apiKey: String
    ) = makeApiCall {
        apiInterface.fetchCreditInfo(
            apiKey
        )
    }
}

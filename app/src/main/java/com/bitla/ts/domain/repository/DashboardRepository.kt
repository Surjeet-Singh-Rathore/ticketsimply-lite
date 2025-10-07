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

    suspend fun newDashboardFetch(
        dashboardFetchRequest: com.bitla.ts.domain.pojo.dashboard_fetch.request.ReqBody
    ) = makeApiCall { apiInterface.newDashBoardFetch(dashboardFetchRequest) }


    suspend fun newOccupancyDetails(
        reqBody: com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.occupancy_model.request.ReqBody
    ) = makeApiCall {
        apiInterface.newOccupancyDetails(
            reqBody.apiKey,
            reqBody.destination.toString(),
            reqBody.from,
            reqBody.originId.toString(),
            reqBody.sortBy,
            reqBody.to
        )
    }



    suspend fun newRevenueDetails(
        reqBody: com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.revenue_model.request.ReqBody
    ) = makeApiCall {
        apiInterface.newRevenueDetails(
            reqBody.apiKey, reqBody.destination.toString(),
            reqBody.from, reqBody.originId.toString(), reqBody.sortBy, reqBody.to
        )
    }


    suspend fun getOccupancyDetails(
        apiKey: String,
        originId: Int,
        destinationId: Int,
        from: String,
        to: String,
        sortBy: String,
        serviceId: String,
        apiType: Int,
        is3DaysData: Boolean,
        locale: String,
        is_from_middle_tier: Boolean
    ) = makeApiCall {
        apiInterface.getOccupancyDetailsApi(
            apiKey,
            originId,
            destinationId,
            from,
            to,
            sortBy,
            serviceId,
            apiType,
            is3DaysData,
            locale,
            is_from_middle_tier
        )
    }
    suspend fun getRevenueDetails(
        apiKey: String,
        originId: Int,
        destinationId: Int,
        from: String,
        to: String,
        sortBy: String,
        serviceId: String,
        branchId: String,
        apiType: Int,
        is3DaysData: Boolean,
        locale: String,
        is_from_middle_tier: Boolean
    ) = makeApiCall {
        apiInterface.getRevenueDetailsApi(
            apiKey,
            originId,
            destinationId,
            from,
            to,
            sortBy,
            serviceId,
            branchId,
            apiType,
            is3DaysData,
            locale,
            is_from_middle_tier
        )
    }


    suspend fun getPerformanceDetails(
        apiKey: String,
        originId: Int,
        destinationId: Int,
        from: String,
        to: String,
        sortBy: String,
        serviceId: String,
        branchId: String,
        apiType: Int,
        is3DaysData: Boolean,
        locale: String,
        is_from_middle_tier: Boolean,
        journeyBy: String?
    ) = makeApiCall {
        apiInterface.getPerformanceDetailsApi(
            apiKey,
            originId,
            destinationId,
            from,
            to,
            sortBy,
            serviceId,
            branchId,
            apiType,
            is3DaysData,
            locale,
            is_from_middle_tier,
            journeyBy
        )
    }


    suspend fun newBookingTrends(
        reqBody: com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.booking_trends_model.request.ReqBody
    ) = makeApiCall {
        apiInterface.newBookingTrendsApi(
            reqBody.apiKey,
            reqBody.destinationId.toString(),
            reqBody.from,
            reqBody.originId.toString(),
            reqBody.sortBy,
            reqBody.to
        )
    }


    suspend fun newServiceWiseBookingDetails(
        reqBody: com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.service_wise_booking_model.request.ReqBody
    ) = makeApiCall {
        apiInterface.newServiceWiseBookingDetails(
            reqBody.apiKey,
            reqBody.date,
            reqBody.from,
            reqBody.to,
            reqBody.occupancyEnd,
            reqBody.occupancyStart,
            reqBody.reservationId,
            reqBody.sortBy,
            reqBody.serviceId
        )
    }


    suspend fun newSchedulesSummaryDetails(
        reqBody: com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.schedules_summary_details.request.ReqBody,
        locale: String,
    ) = makeApiCall {
        apiInterface.newScheduleSummaryDetails(
            reqBody.apiKey, reqBody.originId.toString(), reqBody.destination.toString(),
            reqBody.endDate, reqBody.sortBy, reqBody.startDate, reqBody.serviceId, locale
        )
    }


    suspend fun getPhoneBlocked(
        apiKey: String,
        endDate: String,
        serviceId: String,
        startDate: String,
        sortBy: String,
        reservationId: String,
        ticketStatusFliter: Boolean,
        locale: String,
        is_from_middle_tier: Boolean
    ) = makeApiCall {
        apiInterface.getPhoneBlockedApi(
            apiKey = apiKey,
            endDate = endDate,
            serviceId = serviceId,
            startDate = startDate,
            sortBy = sortBy,
            reservationId = reservationId,
            ticketStatusFliter = ticketStatusFliter,
            locale = locale,
            is_from_middle_tier = is_from_middle_tier
        )
    }




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


    suspend fun newPendingQuota(
        reqBody: com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.pending_quota_model.request.ReqBody
    ) = makeApiCall {
        apiInterface.newPendingQuotaApi(
            reqBody.apiKey,
            reqBody.reservationId,
            reqBody.endDate,
            reqBody.sortBy,
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

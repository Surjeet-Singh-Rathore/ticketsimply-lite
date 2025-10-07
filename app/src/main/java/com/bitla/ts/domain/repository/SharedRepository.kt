package com.bitla.ts.domain.repository

import com.bitla.ts.data.ApiInterface
import com.bitla.ts.domain.pojo.delete_recent_search.request.ReqBody
import com.bitla.ts.domain.pojo.notificationDetails.request.NotificationDetailsRequest
import com.bitla.ts.domain.pojo.update_notification.request.UpdateNotificationRequest
import com.bitla.ts.koin.models.makeApiCall
import com.bitla.ts.utils.security.EncrypDecryp

class SharedRepository(private val apiInterface: ApiInterface) {
    
    suspend fun newRecentSearch(
        apiKey: String,
        limit: Int,
        isBima: Boolean,
        locale: String,
    ) = makeApiCall {
        apiInterface.newRecentSearchApi(
            api_key = apiKey,
            limit = limit,
            locale = locale,
            isBima = isBima
        )
    }


    suspend fun newDeleteRecentSearch(
        deleteRecentSearchRequest: ReqBody
    ) = makeApiCall { apiInterface.newDeleteRecentSearchApi(deleteRecentSearchRequest) }


    suspend fun newGetServiceDetail(
        reservationId: String,
        origin: String,
        destinationId: String,
        apiKey: String,
        operator_api_key: String,
        locale: String,
        app_bima_enabled: Boolean,
        excludePassengerDetails : Boolean
    ) = makeApiCall {
        apiInterface.newGetServiceDetails(
            reservationId = EncrypDecryp.getEncryptedValue(reservationId),
            origin_id = origin,
            destination_id = destinationId,
            api_key = EncrypDecryp.getEncryptedValue(apiKey),
            operator_api_key =  EncrypDecryp.getEncryptedValue(operator_api_key),
            locale = locale,
            app_bima_enabled = app_bima_enabled,
            exclude_passenger_details = excludePassengerDetails,
            is_encrypted = EncrypDecryp.isEncrypted()
        )
    }

    suspend fun getServiceDetailsByRouteId(
        routeId: String,
        origin:String,
        destinationId:String,
        travelDate:String,
        apiKey: String,
        operator_api_key:String,
        locale:String,
        excludePassengerDetails : Boolean
    ) = makeApiCall {
        apiInterface.getServiceDetailsByRouteId(
            routeId,
            origin,
            destinationId,
            travelDate,
            apiKey,
            operator_api_key,
            excludePassengerDetails,
            locale
        )
    }

    suspend fun newGetBpDpServiceDetails(
        reservationId: String,
        apiKey: String,
        operator_api_key:String,
        origin:String,
        destinationId:String,
        boardingAt:String,
        dropOff:String,
        locale:String
    ) = makeApiCall {
        apiInterface.newGetBpDpServiceDetails(
            reservationId = reservationId,
            api_key = apiKey,
            operator_api_key = operator_api_key,
            origin_id = origin,
            destination_id = destinationId,
            locale = locale,
            boardingAt = boardingAt,
            dropOff = dropOff
        )
    }


//    suspend fun newGetBpDpServiceDetails(
//        authorization: String,
//        apiKey: String,
//        bpDpServiceDetailsRequest: BpDpServiceDetailsRequest
//    ) = apiInterface.getBpDpServiceDetails(authorization, apiKey, bpDpServiceDetailsRequest)

    suspend fun getQuickBookServiceDetail(
        reservationId: String,
        origin: String,
        destinationId: String,
        apiKey: String,
        operator_api_key: String,
        locale: String
    ) = makeApiCall {
        apiInterface.getQuickBookServiceDetailsApi(
            reservationId,
            origin,
            destinationId,
            apiKey,
            operator_api_key,
            locale
        )
    }
    suspend fun newBookingSummary(
        apiKey: String,
        reservationId: String,
        responseFormat: String,
        locale: String
    ) = makeApiCall { apiInterface.newBookingSummaryApi( apiKey, reservationId,responseFormat,locale)}


    suspend fun newServiceSummary(
        apiKey: String,
        locale: String,
        reservationId: String,
        reservationFormat: Boolean
    ) = makeApiCall { apiInterface.newServiceSummaryApi(apiKey,locale,reservationId,reservationFormat) }


    suspend fun newCollectionSummary(
        apiKey: String,
        locale: String,
        reservationId: String,
        reservationFormat: String
    ) = makeApiCall { apiInterface.newCollectionSummaryApi(apiKey,locale,reservationId,reservationFormat) }


    suspend fun newReleasedSummary(
        apiKey: String,
        reservationId: String,
        reservationFormat: String,
        locale: String,
    ) = makeApiCall { apiInterface.newReleasedSummaryApi(apiKey,reservationId,reservationFormat,locale) }


    suspend fun newCancellationPoliciesServiceSummary(
        apiKey: String,
        locale: String,
        responseFormat : Boolean,
    )= makeApiCall {  apiInterface.newCancellationPoliciesSummaryApi(apiKey,locale,responseFormat) }


suspend fun newFetchNotification(
        apiKey: String,
        pagination:Boolean,
        perPage:Int,
        page:Int,
        filterType:String,
        currentDay: Int,
        readType: Int,
    ) = makeApiCall { apiInterface.newFetchNotification(
        apiKey,pagination,page,perPage,filterType,currentDay,readType
    )}


    suspend fun notificationDetails(
        authorization: String,
        apiKey: String,
        notificationDetailsRequest: NotificationDetailsRequest
    ) = makeApiCall {
        apiInterface.notificationDetails(
            authorization,
            apiKey,
            notificationDetailsRequest = notificationDetailsRequest
        )
    }
    suspend fun newNotificationDetails(
            apiKey: String,
            notificationID:Int
        ) =  makeApiCall {
        apiInterface.newNotificationDetails(
            apiKey,
            notificationID
        )
    }

    suspend fun updateNotification(
        authorization: String,
        apiKey: String,
        updateNotificationRequest: UpdateNotificationRequest
    ) = makeApiCall {  apiInterface.updateNotification(authorization, apiKey, updateNotificationRequest)}

    suspend fun dragDropRemarksUpdate(
        reqBody: com.bitla.ts.domain.pojo.drag_drop_remarks_update.request.DragDropRemarksUpdateRequest
    ) = makeApiCall { apiInterface.dragDropRemarksUpdate(reqBody) }

    suspend fun frequentTravellersApi(
        apiKey: String,
        reservationId: String,
        locale: String

    ) = makeApiCall { apiInterface.getFrequentTravellerApi(apiKey,reservationId,locale) }
}
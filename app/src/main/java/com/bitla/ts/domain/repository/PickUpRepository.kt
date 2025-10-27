package com.bitla.ts.domain.repository

import com.bitla.ts.data.ApiInterface
import com.bitla.ts.domain.pojo.add_driver.request.ReqBody
import com.bitla.ts.domain.pojo.allotedServiceDirect.AllotedDirctRequest.AllotedDirectRequest
import com.bitla.ts.domain.pojo.alloted_services.request.AllotedServiceRequest
import com.bitla.ts.domain.pojo.bulkCancelOtpConfirmtion.request.BulkCancelVerificationRequest
import com.bitla.ts.domain.pojo.pickUpVanChart.PickUpVanRequest
import com.bitla.ts.domain.pojo.pickUpVanChart.VanChartStatusChangeRequest
import com.bitla.ts.domain.pojo.viewSummary.ViewSummaryRequest
import com.bitla.ts.koin.models.makeApiCall
import com.bitla.ts.utils.security.EncrypDecryp

class PickUpRepository(private val apiInterface: ApiInterface) {
    suspend fun allotedService(
        authorization: String,
        apiKey: String,
        allotedServiceRequest: AllotedServiceRequest,
    ) = makeApiCall {apiInterface.getAllotedServicies(authorization, apiKey, allotedServiceRequest) }

    suspend fun newViewSummary(
        viewSummaryRequest: ViewSummaryRequest
    ) = makeApiCall {
        apiInterface.viewSummaryApi(
            viewSummaryRequest.is_group_by_hubs,
            viewSummaryRequest.hub_id,
            viewSummaryRequest.api_key,
            viewSummaryRequest.travel_date,
            viewSummaryRequest.is_from_middle_tier,
            viewSummaryRequest.view_summary,
            viewSummaryRequest.origin,
            viewSummaryRequest.destination,
            viewSummaryRequest.locale
        )
    }

    suspend fun getAllottedServicesWithDateChange(
        apiKey: String,
        origin: String,
        destination: String,
        from: String,
        to: String,
        hubId: String?,
        isGroupByHubs: Boolean,
        viewMode: String,
        locale: String,
        isFromMiddleTier: Boolean
    ) = makeApiCall {
        apiInterface.getAllottedServicesWithDateChange(
            apiKey,
            origin,
            destination,
            from,
            to,
            hubId,
            isGroupByHubs,
            viewMode,
            locale,
            isFromMiddleTier
        )
    }

    suspend fun getCoachList(
        apiKey: String,
        routeId: String
    ) = makeApiCall {
        apiInterface.getCoachList(
            apiKey,
            routeId
        )
    }

    suspend fun newAllotedService(
        allotedRequest: AllotedDirectRequest
    ) = makeApiCall {
        apiInterface.allotedServiceDirectCall(
            allotedRequest.is_group_by_hubs,
            allotedRequest.hub_id,
            allotedRequest.api_key,
            allotedRequest.travel_date,
            allotedRequest.page,
            allotedRequest.per_page,
            allotedRequest.view_mode,
            allotedRequest.pagination,
            allotedRequest.origin,
            allotedRequest.destination,
            allotedRequest.locale,
            allotedRequest.isCheckingInspector,
            allotedRequest.serviceFilter,
            allotedRequest.res_id
        )
    }

    suspend fun newBlockUnblockReservationService(
        blockUnblockRequest: com.bitla.ts.domain.pojo.block_unblock_reservation.request.ReqBody,
    ) = makeApiCall { apiInterface.newBlockUnblockReservation(blockUnblockRequest) }

    suspend fun getServiceBlockReasonsList(
        apiKey: String
    ) = makeApiCall { apiInterface.getServiceBlockReasonsList(apiKey) }


    suspend fun newLockChartService(
        lockChartRequest: com.bitla.ts.domain.pojo.lock_chart.ReqBody,
    ) = makeApiCall { apiInterface.newLockChartApi(lockChartRequest) }


    suspend fun newAddADHOCDriverService(
        addADHOCDriverRequest: ReqBody,
    ) = makeApiCall { apiInterface.newAddADHOCDriverService(addADHOCDriverRequest) }


    suspend fun newGetEmployeesDetails(
        apiKey: String,
        locale: String,
    ) = makeApiCall { apiInterface.newGetEmployeesDetails(apiKey, locale) }


    suspend fun newGetExpensesDetails(
        apiKey: String,
        reservationId: String,
        locale: String,
        respFormat: String,

        ) = makeApiCall { apiInterface.newGetExpensesDetails(apiKey, reservationId, locale, respFormat) }


    suspend fun newUpdateExpensesDetails(
        updateExpensesDetailsRequest: com.bitla.ts.domain.pojo.update_expenses_details.request.ReqBody,
    ) = makeApiCall { apiInterface.newUpdateExpensesDetails(updateExpensesDetailsRequest) }


    suspend fun newUpdateServiceAllotment(
        id: Long,
        reqBody: com.bitla.ts.domain.pojo.service_allotment.request.ReqBody
    ) = makeApiCall {
        apiInterface.newUpdateServiceAllotment(
            id, reqBody
        )
    }


    suspend fun newViewReservationService(
        apiKey: String,
        resId: String,
        chartType: String,
        locale: String,
        newPickupChart: Boolean?
    ) = makeApiCall { apiInterface.newGetViewReservation(EncrypDecryp.getEncryptedValue(apiKey), EncrypDecryp.getEncryptedValue(resId), chartType, locale, newPickupChart, is_encrypted = EncrypDecryp.isEncrypted()) }
    suspend fun getPickUpVanChart(
        pickUpVanRequest: PickUpVanRequest
    ) = makeApiCall {
        apiInterface.getPickupVanChart(
            EncrypDecryp.getEncryptedValue(pickUpVanRequest.api_key),
            EncrypDecryp.getEncryptedValue(pickUpVanRequest.schedule_id.toString()),
            pickUpVanRequest.locale,
            EncrypDecryp.isEncrypted()
        )
    }



    suspend fun newUpdateBoardedStatusService(
        updatedBoardedStatusRequest: com.bitla.ts.domain.pojo.update_boarded_status.ReqBody

    ) = makeApiCall { apiInterface.newUpdateBoardedStatus(updatedBoardedStatusRequest) }

    suspend fun vanChartUpdatedStatus(
        apiKey: String,
        pnrNumber: String,
        seatNumber: String,
        vanChart: Boolean,
        locale: String,
        vanChartStatusChangeRequest: VanChartStatusChangeRequest

    ) = makeApiCall {
        apiInterface.vanChartStatus(
            apiKey,
            pnrNumber,
            seatNumber,
            vanChart,
            locale,
            vanChartStatusChangeRequest
        )
    }


    suspend fun newUpdateBoardedStatusServiceCargo(
        updateBoardedStartusCargo: com.bitla.ts.domain.pojo.update_boarded_status.request.ReqBody

    ) = makeApiCall { apiInterface.newUpdateBoardedStatusCargo(updateBoardedStartusCargo) }


    suspend fun newPickUpChartPdfService(
        reqBody: com.bitla.ts.domain.pojo.pickup_chart_pdf_url.request.ReqBody

    ) = makeApiCall {
        apiInterface.newGetPickupChartPDF(
            EncrypDecryp.getEncryptedValue(reqBody.api_key),
            EncrypDecryp.getEncryptedValue(reqBody.res_id),
            reqBody.travel_date,
            reqBody.locale!!,
            EncrypDecryp.isEncrypted(),
            reqBody.audit_type
        )
    }


    suspend fun newBulkCancellationService(
        bulkCancellationRequest: com.bitla.ts.domain.pojo.bulk_cancellation.request.ReqBody
    ) = makeApiCall { apiInterface.newBulkCancelation(bulkCancellationRequest) }

    suspend fun newBulkCancelVerification(
        bulkCancellationVerificationRequest: BulkCancelVerificationRequest
    ) = makeApiCall { apiInterface.confirmOtpBulkCancellation(bulkCancellationVerificationRequest) }


    suspend fun newAnnouncementRequest(
        reqBody: com.bitla.ts.domain.pojo.announcement_model.request.ReqBody
    ) = makeApiCall { apiInterface.newAnnouncementRequest(reqBody.apiKey, reqBody.locale!!, reqBody.reservationId) }


    suspend fun newAnnouncementDetailsRequest(
        announcementDetailsApiRequest: com.bitla.ts.domain.pojo.announcement_details_model.request.ReqBody
    ) = makeApiCall { apiInterface.newAnnouncementDetailsRequest(
        announcementDetailsApiRequest
    ) }


    suspend fun newResendOtpAndQrCodeService(
        sendOtpAndQrCodeRequest: com.bitla.ts.domain.pojo.sendOtpAndQrCode.request.ReqBody

    ) = makeApiCall { apiInterface.newResendOtpAndrQrCode(sendOtpAndQrCodeRequest) }


    suspend fun newGetAllCoach(
        apiKey: String,
        resId: String,
        locale: String
    ) = makeApiCall { apiInterface.newGetAllCoaches(apiKey, resId, locale) }


    suspend fun newCollectionDetailsService(
        reqBody: com.bitla.ts.domain.pojo.collection_details.request.ReqBody

    ) = makeApiCall {
        apiInterface.newCollectionDetailApi(
            reqBody.api_key,
            reqBody.reservation_id,
            reqBody.locale!!
        )
    }


    suspend fun tripCollectionDetailsService(
        reqBody: com.bitla.ts.domain.pojo.collection_details.request.ReqBody

    ) = makeApiCall {
        apiInterface.getTripCollectionDetailsApi(
            reqBody.api_key,
            reqBody.reservation_id,
            reqBody.locale!!
        )
    }


    suspend fun updateRateCardTimeService(
        updateRateCardTimeRequest: com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_time.request.ReqBody

    ) =  makeApiCall {  apiInterface.newUpdateRateCardTimeApi(updateRateCardTimeRequest) }


    suspend fun newFetchMultiStatioWiseFareService(
        reqBody: com.bitla.ts.domain.pojo.update_rate_card.multistation_wise_fare.request.ReqBody

    ) = makeApiCall {
        apiInterface.newFetchMultiStatioWiseFareApi(
            reqBody.apiKey,
            reqBody.date,
            reqBody.reservation_id,
            reqBody.channelId,
            reqBody.templateId,
            reqBody.locale!!
        )
    }


    suspend fun newManageMultiStatioWiseFareApi(
        manageFareMultiStationRequest: com.bitla.ts.domain.pojo.update_rate_card.manage_fare_multistaion.request.ReqBody
    ) = makeApiCall {
        apiInterface.newManageMultiStatioWiseFareApi(
            manageFareMultiStationRequest
        )
    }


    suspend fun newUpdateRateCardSeatWiseService(
        updateRateCardSeatWiseRequest: com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_seatwise.by_seat_type.request.ReqBody

    ) = makeApiCall { apiInterface.newUpdateRateCardSeatWiseApi(updateRateCardSeatWiseRequest) }


    suspend fun updateRateCardSeatWisePerSeatService(
        updateRateCardPerSeatRequest: com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_seatwise.per_seat.request.ReqBody

    ) = makeApiCall { apiInterface.newUpdateRateCardSeatWisePerSeatApi(updateRateCardPerSeatRequest) }


    suspend fun newPickupChartCrewDetails(
        apiKey: String,
        resId: String,
        locale: String,
        coachId: String
    ) = makeApiCall {
        apiInterface.newPickupChartCrewDetailsApi(
            EncrypDecryp.getEncryptedValue(apiKey),
            EncrypDecryp.getEncryptedValue(resId),
            locale,
            is_encrypted = EncrypDecryp.isEncrypted(),
            coachId
        )
    }


    suspend fun newCityPickupChartByStageService(
        reqBody: com.bitla.ts.domain.pojo.city_pickup_by_chart_stage.request.ReqBody
    ) = makeApiCall { apiInterface.newCloseChartByCity(reqBody) }


    suspend fun updateTripStatusApi(
        reqBody: Any
    ) = makeApiCall {
        apiInterface.updateTripStatusApi(
            reqBody
        )
    }


    suspend fun updateLuggageOptionIntlApi(
        reqBody: com.bitla.ts.domain.pojo.luggage_details.request.ReqBody
    )  = makeApiCall {
        apiInterface.updateLuggageOptionIntlApi(reqBody)
    }

    suspend fun fetchLuggageDetailsIntlApi(
        apiKey: String,
        pnrNumber: String
    ) = makeApiCall {
        apiInterface.fetchLuggageDetailsIntlApi(apiKey, pnrNumber)
    }
}
package com.bitla.ts.domain.repository

import com.bitla.ts.data.ApiInterface
import com.bitla.ts.domain.pojo.book_ticket_full.request.*
import com.bitla.ts.domain.pojo.book_ticket_full.request.ReqBody
import com.bitla.ts.domain.pojo.campaigns_and_promotions_discount.request.*
import com.bitla.ts.domain.pojo.ezetap.*
import com.bitla.ts.domain.pojo.getCouponDiscount.*
import com.bitla.ts.domain.pojo.getPrefillPassenger.*
import com.bitla.ts.domain.pojo.paytm_pos_integration.paytm_pos_txn_status_api.request.PaytmPosTxnStatusRequest
import com.bitla.ts.domain.pojo.pinelabs.*
import com.bitla.ts.domain.pojo.rapid_booking.request.*
import com.bitla.ts.domain.pojo.rutDiscountDetails.request.*
import com.bitla.ts.koin.models.makeApiCall

class BookingRepository(private val apiInterface: ApiInterface) {

    suspend fun newBookTicketFull(
        reqBody: ReqBody
    ) = makeApiCall {
        apiInterface.newBookTicketMainApi(
            reqBody
        )
    }

    suspend fun bookTicketWithInsurance(
        reqBody: ReqBodyWithInsurance
    ) = makeApiCall {
        apiInterface.bookTicketInsuranceApi(
            reqBody
        ) }

    suspend fun bookTicketWithRapidBooking(
        reqBody: RapidBookingRequest
    ) = makeApiCall {
        apiInterface.bookTicketRapidBookingApi(
            reqBody
        )
    }



    suspend fun newFareBreakup(
        fareBreakupRequest: com.bitla.ts.domain.pojo.fare_breakup.request.ReqBody
    ) = makeApiCall { apiInterface.newFareBreakup(fareBreakupRequest) }

    suspend fun pineLabStatusApi(
        reqBody: ReqBodyPinelab
    ) = makeApiCall { apiInterface.pinelabPaymentStatusApi(reqBody) }

suspend fun shortRouteCityPairAPI(
        apiKey: String,
        resId: String,
    ) = makeApiCall { apiInterface.shortRouteCityPair(apiKey, resId) }


    suspend fun newBookExtraSeat(
        bookExtraSeatRequest: com.bitla.ts.domain.pojo.book_extra_seat.request.ReqBody
    ) = makeApiCall { apiInterface.newBookExtraSeatApi(bookExtraSeatRequest) }

    suspend fun newBookWithExtraSeat(
        reqBody: com.bitla.ts.domain.pojo.book_with_extra_seat.request.BookTicketWithExtraSeatRequest
    ) = makeApiCall {
        apiInterface.newBookWithExtraSeatApi(
            reqBody.apiKey.toString(),
            reqBody.boardingAt.toString(),
            reqBody.destinationId.toString(),
            reqBody.dropOff.toString(),
            reqBody.noOfSeats.toString(),
            reqBody.originId.toString(),
            reqBody.reservationId.toString(),
            reqBody.locale.toString(),
            reqBody.operator_api_key.toString(),
            reqBody.is_from_bus_opt_app.toString(),
            bookExtraSeatRequest = reqBody
        )
    }


    suspend fun newConfirmPhoneBlockTicket(
        confirmPhoneBlockTicketReq: com.bitla.ts.domain.pojo.photo_block_tickets.request.ReqBody
    ) = makeApiCall { apiInterface.newConfirmPhoneBlockTicketApi(confirmPhoneBlockTicketReq) }
    
    suspend fun newConfirmBimaPhoneBlockTicket(
        confirmPhoneBlockTicketReq: com.bitla.ts.domain.pojo.photo_block_tickets.request.ReqBody
    ) = makeApiCall { apiInterface.newConfirmBimaPhoneBlockTicketApi(confirmPhoneBlockTicketReq) }


    suspend fun newShowBookingHistory(
        apiKey: String,
        pnr_number: String,
        response_format: String,
        locale: String
    ) = makeApiCall { apiInterface.newShowBookingHistory(apiKey, pnr_number, response_format, locale)}









    suspend fun getCouponDiscount(
        getCouponDiscountRequest: GetCouponDiscountRequest
    ) = makeApiCall { apiInterface.getCouponDiscountDetails(getCouponDiscountRequest) }

    suspend fun getRutDiscountDetails(
        rutDiscountRequest: RutDiscountRequest
    ) = makeApiCall {
        apiInterface.rutDiscountDetails(
            rutDiscountRequest.seat_number,
            rutDiscountRequest.reservation_id,
            rutDiscountRequest.origin,
            rutDiscountRequest.destination,
            rutDiscountRequest.rut_number,
            rutDiscountRequest.no_of_seats,
            rutDiscountRequest.date,
            rutDiscountRequest.api_key,
            rutDiscountRequest.is_from_middle_tier
        )
    }
    suspend fun getPrefillPassenger(
        getPrefillPassengerRequest: GetPrefillPassengerRequest
    ) = makeApiCall { apiInterface.getPrefillPassenger(
       getPrefillPassengerRequest.card_number,
        getPrefillPassengerRequest.card_type,
        getPrefillPassengerRequest.seat_number,
        getPrefillPassengerRequest.api_key,
        getPrefillPassengerRequest.locale,
        getPrefillPassengerRequest.is_from_middle_tier
    )
    }


    suspend fun newWalletOtpGeneration(
        reqBody: com.bitla.ts.domain.pojo.wallet_otp_generation.request.ReqBody
    ) =  makeApiCall { apiInterface.newWalletOtpGenerationApi(reqBody) }



    /*suspend fun validateOtpWallet(
        authorization: String,
        apiKey: String,
        validateOtpWalletsRequest: ValidateOtpWalletsRequest
    ) = apiInterface.validateOtpWalletApi(authorization, apiKey, validateOtpWalletsRequest) */

    suspend fun validateOtpWallet(
        req_body: com.bitla.ts.domain.pojo.validate_otp_wallets.request.ReqBody
    ) = makeApiCall { apiInterface.newValidateOtpWalletApi(req_body) }

/*    suspend fun upiCreateQrCode(
        authorization: String,
        apiKey: String,
        upiCreateQRCodeRequest: UPICreateQRCodeRequest
    ) = apiInterface.upiCreateQrCodeApi(authorization, apiKey, upiCreateQRCodeRequest)  */

    suspend fun upiCreateQrCode(
        reqBody: com.bitla.ts.domain.pojo.upi_create_qr.request.ReqBody
    ) = makeApiCall { apiInterface.newUpiCreateQrCodeApi(reqBody) }

    suspend fun upiTranxStatus(
        reqBody: com.bitla.ts.domain.pojo.upi_check_status.request.ReqBody
    ) = makeApiCall { apiInterface.newUpiTranxStatus(reqBody) }
    
    
    suspend fun getAgentUpiTranxStatus(
        apiKey: String,
        pnrNumber: String,
//        amount: String,
        phone: String,
        isFromAgentRecharge: String,
    ) = makeApiCall {
        apiInterface.newGetAgentUpiTranxStatusApi(
            apikey = apiKey,
            pnrNumber = pnrNumber,
//            amount = amount,
            phone = phone,
            isFromAgentRecharge = isFromAgentRecharge
        )
    }

    suspend fun getBranchUpiTranxStatus(
        apiKey: String,
        pnrNumber: String,
        branchPhone: String
    ) = makeApiCall {
        apiInterface.getBranchUpiTranxStatusApi(
            apikey = apiKey,
            pnrNumber = pnrNumber,
            branchPhone = branchPhone
        )
    }

    suspend fun campaignsAndPromotionsDiscount(
        campaignsAndPromotionsDiscountRequest: CampaignsAndPromotionsDiscountRequest?
    ) = makeApiCall {
        apiInterface.campaignsAndPromotionsDiscountApi(
            reservationId = campaignsAndPromotionsDiscountRequest?.reservationId,
            api_key = campaignsAndPromotionsDiscountRequest?.api_key,
            operator_api_key = campaignsAndPromotionsDiscountRequest?.operator_api_key,
            locale = campaignsAndPromotionsDiscountRequest?.locale,
            origin_id = campaignsAndPromotionsDiscountRequest?.origin_id,
            destination_id = campaignsAndPromotionsDiscountRequest?.destination_id,
            boardingAt = campaignsAndPromotionsDiscountRequest?.boardingAt,
            dropOff = campaignsAndPromotionsDiscountRequest?.dropOff,
            campaignsAndPromotionsDiscountRequest = campaignsAndPromotionsDiscountRequest?.reqBody
        )
    }

    suspend fun ezetapStatusApi(
        reqBody: ReqBodyEzetapStatus
    ) = makeApiCall { apiInterface.ezetapPaymentStatusApi(reqBody) }

    suspend fun getPaytmPosTxnStatusApi(
        paytmPosTxnStatusRequest: PaytmPosTxnStatusRequest,
    ) = apiInterface.paytmPosTxnStatusApi(paytmPosTxnStatusRequest)

    suspend fun confirmPhonePeV2PendingSeat(
        pnrNumber: String
    ) = makeApiCall { apiInterface.confirmPhonePeV2PendingSeat(pnrNumber) }
}

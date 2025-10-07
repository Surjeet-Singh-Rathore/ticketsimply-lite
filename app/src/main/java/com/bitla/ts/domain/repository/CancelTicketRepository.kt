package com.bitla.ts.domain.repository

import com.bitla.ts.data.ApiInterface
import com.bitla.ts.domain.pojo.bulk_ticket_update.request.ReqBody
import com.bitla.ts.domain.pojo.cancellation_details_model.request.ReqBody2
import com.bitla.ts.koin.models.makeApiCall

class CancelTicketRepository(private val apiInterface: ApiInterface) {


    suspend fun newGetCancelPartialTicketRequest(
        reqBody: com.bitla.ts.domain.pojo.cancel_partial_ticket_model.request.ReqBody
    ) = makeApiCall {
        apiInterface.newGetCancelPartialTicket(
            reqBody.apiKey,
            reqBody.cancelType,
            reqBody.isFromBusOptApp.toString(),
            reqBody.isOnbehalfBookedUser.toString(),
            reqBody.json_format,
            reqBody.locale!!,
            reqBody.onbehalf_online_agent_flag.toString(),
            reqBody.operatorApiKey,
            reqBody.passengerDetails,
            reqBody.responseFormat,
            reqBody.seatNumbers,
            reqBody.ticketCancellationPercentageP,
            reqBody.ticketNumber,
            reqBody.travelDate,
            reqBody.zeroPercent.toString(),
            reqBody.is_sms_send,
            reqBody.isBimaTicket ?: false,
            reqBody.authPin,
            reqBody.remarkCancelTicket
        )
    }


//    suspend fun newGetConfirmOtpCancelPartialTicketRequest(
//        authorization: String,
//        apiKey: String,
//        confirmOtpCancelPartialTicketRequest: ConfirmOtpCancelPartialTicketRequest
//    ) =
//        apiInterface.getConfirmOtpCancelPartialTicket(
//            authorization,
//            apiKey,
//            confirmOtpCancelPartialTicketRequest
//        )

    suspend fun newGetConfirmOtpCancelPartialTicketRequest(
        reqBody: com.bitla.ts.domain.pojo.confirm_otp_cancel_partial_ticket_model.request.ReqBody
    ) = makeApiCall {
        apiInterface.newGetConfirmOtpCancelPartialTicket(
            apikey = reqBody.apiKey,
            key = reqBody.key,
            cancel_type = reqBody.cancelType,
            is_from_bus_opt_app = reqBody.isFromBusOptApp.toString(),
            is_onbehalf_booked_user = reqBody.isOnbehalfBookedUser.toString(),
            json_format = reqBody.json_format,
            locale = reqBody.locale!!,
            onbehalf_online_agent_flag = reqBody.onbehalf_online_agent_flag.toString(),
            operator_api_key = reqBody.operatorApiKey,
            passenger_details = reqBody.passengerDetails,
            response_format = reqBody.responseFormat,
            seat_numbers = reqBody.seatNumbers,
            ticket_cancellation_percentage_p = reqBody.ticketCancellationPercentageP,
            ticket_number = reqBody.ticketNumber,
            travel_date = reqBody.travelDate,
            zero_percent = reqBody.zeroPercent.toString(),
            otp = reqBody.otp,
            isBimaTicket = reqBody.isBimaTicket ?: false
        )
    }

//    suspend fun getCancelTicketRequest(
//        authorization: String,
//        apiKey: String,
//        cancellationDetailsRequest: CancellationDetailsRequest
//    ) =
//        apiInterface.getCancellationDetailsTicket(authorization, apiKey, cancellationDetailsRequest)

    suspend fun newGetCancelTicketRequest(
        reqBody: com.bitla.ts.domain.pojo.cancellation_details_model.request.ReqBody
    ) = makeApiCall {
        apiInterface.newGetCancellationDetailsTicket(
            reqBody.apiKey,
            reqBody.cancelType,
            reqBody.ticketCancellationPercentageP,
            reqBody.isFromBusOptApp.toString(),
            reqBody.isFromMiddleTier.toString(),
            reqBody.json_format,
            reqBody.locale ?: "",
            reqBody.operatorApiKey,
            reqBody.passengerDetails,
            reqBody.pnrNumber,
            reqBody.responseFormat,
            reqBody.seatNumbers,
            reqBody.zeroPercent.toString(),
            reqBody.isBimaTicket ?: false
        )
    }
//    suspend fun getZeroCancellationDetailsTicket(
//        authorization: String,
//        apiKey: String,
//        cancellationDetailsRequest: ZeroCancellationDetailsRequest
//    ) =
//        apiInterface.getZeroCancellationDetailsTicket(
//            authorization,
//            apiKey,
//            cancellationDetailsRequest
//        )

    suspend fun newGetZeroCancellationDetailsTicket(
        reqBody: ReqBody2
    ) = makeApiCall {
        apiInterface.newGetZeroCancellationDetailsTicket(
            reqBody.apiKey,
            reqBody.cancelType,
            reqBody.isFromBusOptApp.toString(),
            reqBody.is_from_middle_tier.toString(),
            reqBody.json_format,
            reqBody.locale!!,
            reqBody.operatorApiKey,
            reqBody.passengerDetails,
            reqBody.pnrNumber,
            reqBody.responseFormat,
            reqBody.seatNumbers,
            reqBody.zeroPercent.toString(),
            reqBody.isBimaTicket ?: false
        )
    }

    suspend fun newGetBulkTicketUpdateApiRequest(
        bulkTicketUpdateRequestModel: ReqBody
    ) = makeApiCall {
        apiInterface.newGetBulkTicketUpdate(bulkTicketUpdateRequestModel)
    }


    suspend fun newGetConfirmOtpReleaseTicketRequest(
        confirmOtpReleasePhoneBlockTicketRequest: com.bitla.ts.domain.pojo.confirm_otp_release_phone_block_tickets_model.request.ReqBody
    ) = makeApiCall {
        apiInterface.newGetConfirmOtpReleasePhoneBlockTicketRequest(
            confirmOtpReleasePhoneBlockTicketRequest
        )
    }

}

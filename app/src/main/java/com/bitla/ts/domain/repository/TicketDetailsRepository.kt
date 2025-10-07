package com.bitla.ts.domain.repository

import com.bitla.ts.data.ApiInterface
import com.bitla.ts.domain.pojo.send_sms_email.request.ReqBody
import com.bitla.ts.domain.pojo.send_sms_email.request.SendSMSEmailRequest
import com.bitla.ts.domain.pojo.ticket_details.request.TicketDetailsRequest
import com.bitla.ts.koin.models.makeApiCall
import com.bitla.ts.utils.security.EncrypDecryp
import retrofit2.http.Query


class TicketDetailsRepository(private val apiInterface: ApiInterface) {

    suspend fun newTicketDetails(
        apiKey: String,
        ticketNumber: String,
        json_format: Boolean,
        isQrScan : Boolean,
        locale: String

    ) = makeApiCall { apiInterface.newTicketDetails(apiKey,ticketNumber,json_format,isQrScan,locale) }


    suspend fun ticketDetailsPhase3(
        apiKey: String,
        ticketNumber: String,
        json_format: Boolean,
        isQrScan : Boolean,
        locale: String,
        loadPrivs: Boolean,
        menuPrivilege: Boolean

    ) = makeApiCall { apiInterface.ticketDetailsPhase3(EncrypDecryp.getEncryptedValue(apiKey),EncrypDecryp.getEncryptedValue(ticketNumber),json_format,isQrScan,locale, loadPrivs, menuPrivilege,EncrypDecryp.isEncrypted()) }

    suspend fun ticketDetailsMenus(
        apiKey: String,
        ticketNumber: String,
        json_format: Boolean,
        locale: String
    ) = makeApiCall { apiInterface.ticketDetailsMenus(apiKey,ticketNumber,json_format,locale) }


    suspend fun newSendSmsEmail(
        sendSMSEmailRequest: ReqBody
        ) = makeApiCall { apiInterface.newSendSMSEmailApi(sendSMSEmailRequest) }

    suspend fun updatePrintCountApi(
        pnr: String,
        isUpdatePrintCount: Boolean,
        api_key:String
        ) = makeApiCall {  apiInterface.updatePrintCountApi(pnr,isUpdatePrintCount,api_key) }
}

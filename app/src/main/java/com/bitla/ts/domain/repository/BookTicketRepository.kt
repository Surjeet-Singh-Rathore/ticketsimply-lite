package com.bitla.ts.domain.repository

import com.bitla.ts.data.ApiInterface
import com.bitla.ts.domain.pojo.book_ticket.request.ReqBody
import com.bitla.ts.koin.models.makeApiCall

class BookTicketRepository(private val apiInterface: ApiInterface) {

    suspend fun newBookTicket(
        bookTicketRequest: ReqBody
    ) = makeApiCall { apiInterface.newBookTicketApi(bookTicketRequest) }

    suspend fun rapidBooking(
        reqBody: Any
    ) =
       makeApiCall {
           apiInterface.newRapidBookingApi(
               reqBody
           )
       }
}

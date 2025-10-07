package com.bitla.ts.domain.repository

import com.bitla.ts.data.ApiInterface
import com.bitla.ts.domain.pojo.add_rate_card.createRateCard.request.CreateRateCardReqBody
import com.bitla.ts.domain.pojo.add_rate_card.deleteRateCard.request.DeleteRateCardReqBody
import com.bitla.ts.domain.pojo.add_rate_card.editRateCard.request.EditRateCardReqBody
import com.bitla.ts.domain.pojo.add_rate_card.fetchRouteWiseFareDetails.request.FetchRouteWiseFareReqBody
import com.bitla.ts.domain.pojo.add_rate_card.fetchShowRateCard.request.FetchShowRateCardReqBody
import com.bitla.ts.domain.pojo.add_rate_card.viewRateCard.request.ViewRateCardReqBody
import com.bitla.ts.koin.models.makeApiCall

class AddRateCardRepository(private val apiInterface: ApiInterface) {

    suspend fun fetchShowRateCardApiService(
        reqBody: FetchShowRateCardReqBody,
    ) = makeApiCall {
        apiInterface.fetchShowRareCardApi(
            reqBody.apiKey,
            reqBody.routeId,
            reqBody.locale
        )
    }

    suspend fun fetchRouteWiseFareApiService(
        reqBody: FetchRouteWiseFareReqBody,
    ) = makeApiCall {
        apiInterface.fetchRouteWiseFareDetailsApi(
            reqBody.apiKey,
            reqBody.routeId,
            reqBody.locale
        )
    }

    suspend fun createRateCardApiService(
        reqBodyCreateRateCard: CreateRateCardReqBody,
    ) = makeApiCall {
        apiInterface.createRateCardApi(
            reqBodyCreateRateCard
        )
    }
    suspend fun editRateCardApiService(
        reqBodyEditRateCard: EditRateCardReqBody,
    ) = makeApiCall {
        apiInterface.editRateCardApi(
            reqBodyEditRateCard
        )
    }

    suspend fun viewRateCardApiService(
        reqBody: ViewRateCardReqBody,
    ) = makeApiCall {
        apiInterface.viewRareCardApi(
            reqBody.apiKey,
            reqBody.rateCardId,
            reqBody.locale
        )
    }

    suspend fun deleteRateCardApiService(
        reqBody: DeleteRateCardReqBody,
    ) = makeApiCall {
        apiInterface.deleteRareCardApi(
            reqBody.apiKey,
            reqBody.rateCardId,
            reqBody.locale
        )
    }
}
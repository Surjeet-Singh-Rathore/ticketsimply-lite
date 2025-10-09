package com.bitla.ts.domain.repository

import com.bitla.ts.data.ApiInterface
import com.bitla.ts.domain.pojo.get_coach_details.request.CoachDetailsRequest
import com.bitla.ts.domain.pojo.get_coach_documents.request.CoachDocumentsRequest
import com.bitla.ts.koin.models.makeApiCall

class VehicleDetailsRepository(private val apiInterface: ApiInterface) {

    suspend fun getCoachDetails(
        coachDetailsRequest: CoachDetailsRequest
    ) = makeApiCall {
        apiInterface.getCoachDetails(
            isFromMiddleTier = coachDetailsRequest.isFromMiddleTier,
            apiKey = coachDetailsRequest.apiKey,
            operatorApiKey = coachDetailsRequest.operatorApiKey,
            locale = coachDetailsRequest.locale
        )
    }

    suspend fun getCoachDocuments(
        coachDocumentsRequest: CoachDocumentsRequest
    ) = makeApiCall {
        apiInterface.getCoachDocuments(

            isFromMiddleTier = coachDocumentsRequest.isFromMiddleTier,
            coachNumber = coachDocumentsRequest.coachNumber,
            apiKey = coachDocumentsRequest.apiKey,
            operatorApiKey = coachDocumentsRequest.operatorApiKey,
            locale = coachDocumentsRequest.locale,
            coachId = coachDocumentsRequest.coachId!!
        )
    }



}
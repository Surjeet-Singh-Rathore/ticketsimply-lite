package com.bitla.ts.domain.repository
import com.bitla.ts.data.ApiInterface

class BookingSummaryRepository(private val apiInterface: ApiInterface) {
    suspend fun newBookingSummaryDetailsService(apiKey: String, resId: String) = apiInterface.newGetBookingSummary(apiKey,resId)
}
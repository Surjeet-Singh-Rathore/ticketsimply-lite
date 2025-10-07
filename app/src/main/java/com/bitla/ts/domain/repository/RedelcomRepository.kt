package com.bitla.ts.domain.repository

import com.bitla.ts.data.ApiInterface
import com.bitla.ts.koin.models.makeApiCall


class RedelcomRepository(private val apiInterface: ApiInterface) {

    suspend fun RedelcomPgStatus(
        apiKey: String,
        locale: String,
        pnr_number: String,
        terminal_id: String,
    ) = makeApiCall { apiInterface.apiGetRedelcomPgStatus(pnr_number,terminal_id,apiKey,locale) }





}

package com.bitla.ts.domain.repository

import com.bitla.ts.data.ApiInterface
import com.bitla.ts.domain.pojo.privilege_details_model.request.PrivilegeRequestModel
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.koin.models.makeApiCall
import com.bitla.ts.koin.networkModule.NetworkProcess
import kotlinx.coroutines.flow.Flow

class PrivilegeRepository(private val apiInterface: ApiInterface) {

    suspend fun getNewPrivilegeDetails(apiKey: String, respFormat: String,locale : String): Flow<NetworkProcess<PrivilegeResponseModel>> {
      return makeApiCall {
            apiInterface.getNewPrevilegeDetailApi(apiKey, respFormat,locale)
        }
    }

}
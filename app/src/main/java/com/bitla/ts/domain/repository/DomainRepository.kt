package com.bitla.ts.domain.repository

import com.bitla.ts.data.ApiInterface
import com.bitla.ts.domain.pojo.dynamic_domain.DynamicDomain
import com.bitla.ts.koin.models.makeApiCall
import com.bitla.ts.koin.networkModule.NetworkProcess
import kotlinx.coroutines.flow.Flow

class DomainRepository(private val apiInterface: ApiInterface) {
    suspend fun initDomain(): Flow<NetworkProcess<DynamicDomain>> {
        return makeApiCall{
                apiInterface.initDomain()
            }
    }


}


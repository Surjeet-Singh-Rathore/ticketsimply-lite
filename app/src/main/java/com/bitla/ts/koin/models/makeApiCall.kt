package com.bitla.ts.koin.models

import com.bitla.ts.app.base.TsApplication
import com.bitla.ts.koin.networkModule.NetworkProcess
import com.bitla.ts.utils.common.firebaseLogEvent
import com.bitla.ts.utils.constants.API_CRASH
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response


suspend inline fun <T> makeApiCall(crossinline apiCall: suspend () -> Response<T>): Flow<NetworkProcess<T>> =
    flow {
        try {
            val response = apiCall()
            emit(NetworkProcess.Loading)
            when {
                response.isSuccessful -> {
                    val body = response.body()
                    body?.let {
                        emit(NetworkProcess.Success(body))
                    }
                }

                else -> {
//                    val loginModelPref = PreferenceUtils.getLogin()
//                    firebaseLogEvent(
//                        TsApplication.getAppContext(),
//                        API_CRASH,
//                        loginModelPref?.userName,
//                        loginModelPref?.travels_name,
//                        loginModelPref?.role,
//                        API_CRASH,
//                        "Api - ${response.raw().request.url} \nResponseErrorBody - ${response.errorBody()} \nResponseBody - ${response.body()}"
//                    )
                    emit(
                        NetworkProcess.Failure(
                            "${response.code()} " + (response.errorBody()?.string() ?: "Something went Wrong"),
                            null,
                           errorCode =  response.code()
                        )
                    )

                }

            }

        } catch (e: Exception) {
            emit(NetworkProcess.Failure(e.message.toString(), null))
        }
    }.flowOn(Dispatchers.IO)

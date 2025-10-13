package com.bitla.ts.koin.networkModule

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import com.bitla.ts.app.base.TsApplication
import com.bitla.ts.utils.common.convertLongToTime
import com.bitla.ts.utils.common.firebaseLogEvent
import com.bitla.ts.utils.common.getLogFileName
import com.bitla.ts.utils.common.getTodayDate
import com.bitla.ts.utils.constants.API_CRASH
import com.bitla.ts.utils.constants.DATE_FORMAT_D_M_Y
import com.bitla.ts.utils.sharedPref.PREF_DOMAIN
import com.bitla.ts.utils.sharedPref.PREF_EXCEPTION
import com.bitla.ts.utils.sharedPref.PREF_FCM_TOKEN
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import io.sentry.BuildConfig
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.internal.http2.ConnectionShutdownException
import okio.Buffer
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Taiyab Ali on 05-Aug-21.
 */
class ApiResponseInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()

        return try {
            val response = chain.proceed(request)
            val bodyString = response.body

            response.newBuilder()
                .body(bodyString)
                .build()

        } catch (e: Exception) {
            var msg = ""
            val interceptorCode = 408
            when (e) {
                is SocketTimeoutException -> {
                    msg = "Timeout - Please check your internet connection"
                }
                is UnknownHostException -> {
                    msg = "Please enter a valid domain" //as discussed with sourabh, we are modifying this toast
                }
                is ConnectionShutdownException -> {
                    msg = "Connection shutdown. Please check your internet"
                }
                is IOException -> {
                    msg = "Server is unreachable, please try again later."
                }
                is IllegalStateException -> {
                    msg = "${e.message}"
                }
                else -> {

                    msg = "${e.message}"

                }
            }

            PreferenceUtils.putString(PREF_EXCEPTION,msg)


            return Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(interceptorCode)
                .message(msg)
                .body("{${msg}}".toResponseBody(null)).build()
        }
    }
}






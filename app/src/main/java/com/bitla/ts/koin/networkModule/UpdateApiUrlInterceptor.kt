package com.bitla.ts.koin.networkModule

import com.bitla.ts.*
import com.bitla.ts.app.base.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.sharedPref.*
import okhttp3.*
import java.io.*

/**
 * Created by Taiyab Ali on 25-May-22.
 */

class UpdateApiUrlInterceptor : Interceptor {

    private var newUrl: HttpUrl? = null

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val host: String = PreferenceUtils.getUpdatedApiUrlAddress()

        try {
            if (host.contains(":")) {
                val prefix = host.substringBefore(":")
                val suffix = host.substringAfter(":").toInt()
                newUrl = request.url.newBuilder()
                    .host(prefix).port(suffix)
                    .build()
            } else {
                newUrl = request.url.newBuilder()
                    .host(host)
                    .build()
            }
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
        }

        // Initial request (default scheme from the URL)
        return if (newUrl != null) {
            try {
                request = request.newBuilder()
                    .url(newUrl!!)
                    .build()
                chain.proceed(request)
            } catch (e: IOException) {
                // Retry with HTTPS if the initial request fails and the current scheme is HTTP
                if (request.url.scheme == "http") {
                    try {
                        val httpsUrl = request.url.newBuilder()
                            .scheme("https")
                            .build()
                        val httpsRequest = request.newBuilder()
                            .url(httpsUrl)
                            .build()
                        chain.proceed(httpsRequest)
                    } catch (httpsEx: Exception) {
                        // Log failure if HTTPS retry also fails
//                        val loginModelPref = PreferenceUtils.getLogin()
//                        firebaseLogEvent(
//                            TsApplication.getAppContext(),
//                            API_CRASH,
//                            loginModelPref?.userName,
//                            loginModelPref?.travels_name,
//                            loginModelPref?.role,
//                            API_CRASH,
//                            "Api - ${request.url} \nResponse - ${httpsEx.message ?: ""}"
//                        )
                        throw IOException("Both HTTP and HTTPS requests failed: ${httpsEx.message}", httpsEx)
                    }
                } else {
                    // If HTTPS was already tried or the error is unrelated to the scheme
                    throw IOException("Request failed: ${e.message}", e)
                }
            }
        } else {
            // Log or handle the case where newUrl couldn't be built
            throw IOException("Failed to modify URL. Original host: $host")
        }
    }
}

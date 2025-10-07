package com.bitla.ts.koin.networkModule

import com.bitla.ts.*
import com.bitla.ts.utils.sharedPref.*
import com.google.gson.*
import okhttp3.*
import okhttp3.logging.*
import org.koin.dsl.*
import retrofit2.*
import retrofit2.converter.gson.*
import timber.log.Timber
import java.util.concurrent.*


val NetworkModule = module {
    factory { provideGson() }
    factory { provideOkHttpClient() }
    single { provideRetrofit(get(), get()) }
}

fun provideGson(): Gson {
    return GsonBuilder()
        .setLenient()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()
}

fun provideOkHttpClient() = if (BuildConfig.DEBUG) {
    val loggingInterceptor = HttpLoggingInterceptor()
    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

    OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(ApiResponseInterceptor())
        .addInterceptor(UpdateApiUrlInterceptor())
        .readTimeout(120, TimeUnit.SECONDS)
        .connectTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .build()
} else {
    OkHttpClient
        .Builder()
        .addInterceptor(ApiResponseInterceptor())
        .addInterceptor(UpdateApiUrlInterceptor())
        .readTimeout(120, TimeUnit.SECONDS)
        .connectTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .build()
}

fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
    // Retrieve base domain from preferences or use a default
    var baseUrl = PreferenceUtils.getPreference(PREF_DOMAIN, "mba.ticketsimply.com")?.trim() ?: "mba.ticketsimply.com"

    // Validate the full URL
    baseUrl = if (PreferenceUtils.getIsHttpsSupport()) {
        "https://$baseUrl"
    } else {
        "http://$baseUrl"
    }

    // base URL ends with a slash as required by Retrofit
    if (!baseUrl.endsWith("/")) {
        baseUrl += "/"
    }

    Timber.d("Base URL: $baseUrl")

    // Validate the URL format
//    if (!android.util.Patterns.WEB_URL.matcher(baseUrl).matches()) {
//        throw IllegalArgumentException("Invalid base URL: $baseUrl")
//    }

    try {
        val parsed = java.net.URL(baseUrl)
        if (parsed.protocol != "http" && parsed.protocol != "https") {
            throw IllegalArgumentException("Invalid protocol in base URL: $baseUrl")
        }
    } catch (e: Exception) {
        baseUrl = "https://mba.ticketsimply.com/"
    }

    // Build and return the Retrofit instance
    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        //.addCallAdapterFactory(CoroutineCallAdapterFactory())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
}
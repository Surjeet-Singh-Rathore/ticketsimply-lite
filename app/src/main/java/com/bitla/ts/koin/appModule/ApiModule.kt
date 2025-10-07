package com.bitla.ts.koin.appModule

import com.bitla.ts.data.ApiInterface
import org.koin.dsl.module
import retrofit2.Retrofit

val ApiModule = module {

    single { provideUseApi(get()) }
}

fun provideUseApi(retrofit: Retrofit): ApiInterface {
    return retrofit.create(ApiInterface::class.java)
}

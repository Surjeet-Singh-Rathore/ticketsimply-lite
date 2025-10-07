package com.bitla.ts.koin.networkModule

sealed class NetworkProcess<out T>() {
    class Success< T>(val data: T) : NetworkProcess<T>()
    class Failure<T>(val message: String, val data: T? = null, val errorCode:Int?=null) : NetworkProcess<T>()
    object Loading : NetworkProcess<Nothing>()
}
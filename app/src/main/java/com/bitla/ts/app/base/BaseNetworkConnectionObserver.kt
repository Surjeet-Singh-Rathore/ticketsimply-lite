package com.bitla.ts.app.base

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkInfo
import android.os.Build
import androidx.lifecycle.LiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

class BaseNetworkConnectionObserver(private val context: Context) : LiveData<Boolean>() {

    private val connectivityManager: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private var networkConnectionCallback: ConnectivityManager.NetworkCallback? = null
    private var isLost = false

    @SuppressLint("MissingPermission")
    override fun onActive() {
        super.onActive()

        try {
            updateNetworkConnection()
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
                    connectivityManager.registerDefaultNetworkCallback(connectivityManagerCallback())
                }
                else -> {
                    context.registerReceiver(
                        networkReceiver,
                        IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
                    )
                }
            }

        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.message?.let { Timber.d("ExceptionMsg", it) }
        }
    }

    override fun onInactive() {
        super.onInactive()
        /*try {
            connectivityManager.unregisterNetworkCallback(connectivityManagerCallback())
        } catch (e: Exception) {
            Timber.d("Inactive Exception- $e")
        }*/

        if (networkConnectionCallback != null && connectivityManager != null) {
            try {
                connectivityManager.unregisterNetworkCallback(networkConnectionCallback!!)
                // Added code: Set call back to null so this doesn't get called again.
                networkConnectionCallback = null
            } catch (e: java.lang.Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                e.message?.let { Timber.d("ExceptionMsg", it) }
            }
        }
    }

    private fun connectivityManagerCallback(): ConnectivityManager.NetworkCallback {
        networkConnectionCallback = object : ConnectivityManager.NetworkCallback() {
            @SuppressLint("MissingPermission")

            override fun onLost(network: Network) {
                super.onLost(network)

                postValue(false)
                isLost = true
            }

            override fun onAvailable(network: Network) {
                super.onAvailable(network)

                if (isLost) {
                    postValue(true)
                }
                isLost = false

                // val networkInfo = connectivityManager.activeNetwork
                // Timber.d("networkInfo onAvailable $networkInfo isLost $isLost")
//                Timber.d("networkInfo after isLost $isLost")
            }
        }
        return networkConnectionCallback as ConnectivityManager.NetworkCallback
    }

    private val networkReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            try {
                updateNetworkConnection()
            } catch (e: Exception) {
                Timber.d("BroadcastOnReceiver Exception- $e")
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateNetworkConnection() {
        try {
            val activeNetworkConnection: NetworkInfo? = connectivityManager.activeNetworkInfo
            if (isLost) {
                postValue(activeNetworkConnection?.isConnected == true)
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.message?.let { Timber.d("ExceptionMsg", it) }
        }
    }
}
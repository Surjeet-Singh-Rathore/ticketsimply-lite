package com.bitla.restaurant_app.presentation.utils

import android.content.Context
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Build
import android.view.View
import android.widget.Toast
import com.bitla.restaurant_app.presentation.utils.Constants.DATE_FORMAT_D_M_Y
import com.bitla.restaurant_app.presentation.utils.Constants.DATE_FORMAT_Y_M_D
import me.drakeet.support.toast.ToastCompat
import timber.log.Timber
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Context.toast(message: String?) {

    if (message != null) {
        if (Build.VERSION.SDK_INT == 25) {
            ToastCompat.makeText(this, message, Toast.LENGTH_SHORT)
                .setBadTokenListener { toast -> Timber.e("failed toast", message) }.show()
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}


fun getDateYMD(dateDMY: String): String {
    var dateYmd = ""
    try {
        if (dateDMY.isNotEmpty()) {
            val sdfDMY = SimpleDateFormat(DATE_FORMAT_D_M_Y)
            val sdfYMD = SimpleDateFormat(DATE_FORMAT_Y_M_D)
            dateYmd = sdfYMD.format(sdfDMY.parse(dateDMY))
            return dateYmd
        }
    } catch (e: Exception) {
        Timber.d("exceptionMsg ${e.message}")
    }
    return dateYmd
}


fun getTodayDate(): String {
    val sdf = SimpleDateFormat(DATE_FORMAT_D_M_Y, Locale.getDefault())
    return sdf.format(Date())
}

fun setDateLocale(locale: String, context: Context) {
    val languageToLoad = locale

    val locale = Locale(languageToLoad)
    Locale.setDefault(locale)
    val config = Configuration()
    config.locale = locale
    context.resources.updateConfiguration(config, context.resources.getDisplayMetrics());
}

fun Context.isNetworkAvailable(): Boolean {
    val connMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = connMgr.activeNetworkInfo
    return activeNetwork != null && activeNetwork.isAvailable && activeNetwork.isConnected
}


fun Double.convert(currencyFormat: String?): String {
    try {
        if (this == null || this == 0.0 || this.toString().isEmpty())
            return "0.0"
        var currencyWithDash = ""
        val format = if (currencyFormat?.contains("-") == true) {
            currencyWithDash = currencyFormat
            DecimalFormat(currencyFormat.replace("-", ","))
        } else
            DecimalFormat(currencyFormat)

        format.isDecimalSeparatorAlwaysShown = false
        return if (currencyWithDash.contains("-"))
            format.format(this).toString().replace(",", ".")
        else
            format.format(this).toString()
    } catch (e: Exception) {
        return "0.0"
    }
}


fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}
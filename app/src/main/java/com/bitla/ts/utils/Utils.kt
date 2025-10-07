package com.bitla.ts.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import com.bitla.ts.R
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.phonepe.intent.sdk.api.PhonePeKt
import com.phonepe.intent.sdk.api.models.PhonePeEnvironment
import toast

fun <T> List<T>?.toArrayList():ArrayList<T>{
    val arrayList = ArrayList<T>()
    this?.forEach {
        arrayList.add(it)
    }
    return arrayList
}

fun Context.showToast(msg:String){
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

inline fun Activity.isActivityIsLive(isActive:()->Unit){
    if(!this.isFinishing){
        isActive()
    }
}


fun getUpiApps(context: Context): List<ResolveInfo> {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.setData(Uri.parse("upi://pay"))
    val packageManager = context.packageManager
    val upiApps = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
    return upiApps
}

fun isUpiAppPresent(context: Context):Boolean {
    return getUpiApps(context).isNotEmpty()
    //return false
}

fun JsonElement?.getNullSafeString(): String {
    return if (this != null && !isJsonNull) asString else ""
}

fun JsonObject.isPresentAndNotNull(key: String): Boolean {
    return this.has(key) && !this.get(key).isJsonNull
}

fun openPhonePeV2(
    context: Context,
    activityResultLauncher: ActivityResultLauncher<Intent>,
    isLiveEnvironment: Boolean,
    merchantId: String,
    flowId: String,
    token: String,
    orderId: String
) {
    val environment = if (isLiveEnvironment) {
        PhonePeEnvironment.RELEASE
    } else {
        PhonePeEnvironment.SANDBOX
    }
    val result = PhonePeKt.init(
        context = context,
        merchantId = merchantId,
        flowId = flowId,
        phonePeEnvironment = environment,
        enableLogging = false,
        appId = null
    )

    if (result) {
        try {
            PhonePeKt.startCheckoutPage(
                context = context,
                token = token,
                orderId = orderId,
                activityResultLauncher = activityResultLauncher
            )
        } catch(ex: Exception) {
            context.toast(context.getString(R.string.something_went_wrong))
        }
    } else {
        context.toast(context.getString(R.string.something_went_wrong))
    }
}
package com.bitla.ts.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import timber.log.Timber

/**
 * Created by Surjeet Rathore on 17/08/22.
 */

class BatteryLevelReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        Timber.d("batteryLevel $level")
    }

}
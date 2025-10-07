package com.bitla.ts.utils

import android.content.Context
import android.content.res.Configuration
import android.util.Log
import com.bitla.ts.utils.constants.DEFAULT_TEXT_SIZE
import com.bitla.ts.utils.constants.LARGE_TEXT_SIZE
import com.bitla.ts.utils.constants.SMALL_TEXT_SIZE
import com.bitla.ts.utils.constants.XLARGE_TEXT_SIZE
import com.bitla.ts.utils.sharedPref.PreferenceUtils

object FontScaleContextWrapper {

    fun wrap(context: Context): Context {
        val config = Configuration(context.resources.configuration)
        config.fontScale = getCustomFontScale(context)

        Log.d("FontScaleWrapper", "Applied font scale: ${config.fontScale}")
        return context.createConfigurationContext(config)
    }

    private fun getCustomFontScale(context: Context): Float {
        return when (PreferenceUtils.getTextSize(context)) {
            SMALL_TEXT_SIZE -> 0.85f
            DEFAULT_TEXT_SIZE -> 1.0f
            LARGE_TEXT_SIZE -> 1.15f
            XLARGE_TEXT_SIZE -> 1.3f
            else -> 1.0f
        }
    }
}

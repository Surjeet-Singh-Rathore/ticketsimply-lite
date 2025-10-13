package com.bitla.ts.phase2.chartUtils

import android.content.Context
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.github.mikephil.charting.formatter.PercentFormatter
import java.text.DecimalFormat
import kotlin.math.abs
import kotlin.math.roundToInt

sealed class IntValueFormatter {

    class IntValueDecimalFormat(format: DecimalFormat?) : PercentFormatter() {
        override fun getFormattedValue(value: Float): String {
//            return if (value < 10) "" else mFormat.format(value).toString() + "%"
            return mFormat.format(value).toString() + "%"
        }

        init {
            this.mFormat = format
        }
    }

    class IntValueFormatCurrency(format: DecimalFormat?, context: Context) : PercentFormatter() {

        private val currency = (context as BaseActivity).getPrivilegeBase()?.currency ?: ""
        private val country = (context as BaseActivity).getPrivilegeBase()?.country


        override fun getFormattedValue(value: Float): String {

            var numberString = ""
            /*if (abs(value.roundToInt() / 1000000) > 1) {
                ("₹" + value.roundToInt() / 1000000) + "M"
            } else */

            if(country == "Indonesia") {
                numberString = if (abs(value.roundToInt() / 1000000000) > 1) {
                    (currency + value.roundToInt() / 1000000000) + "M"
                } else if (abs(value.roundToInt() / 1000000) >= 1) {
                    (currency + value.roundToInt() / 1000000) + "Jt"

                } else if (abs(value.roundToInt() / 1000) >= 1) {
                    (currency + value.roundToInt() / 1000) + "Rb"

                } else if (value.toString() == "0.0" || value.toString() == "0" || value == 0.0f) {
                    currency + 0
                } else {
                    currency + value.roundToInt().toString()
                }
            } else {

                numberString = if (abs(value.roundToInt() / 100000) > 1) {
                    (currency + value.roundToInt() / 100000) + "L"
                } else if (abs(value.roundToInt() / 1000) >= 1) {
                    (currency + value.roundToInt() / 1000) + "k"

                } else if (value.toString() == "0.0" || value.toString() == "0" || value == 0.0f) {
                    currency + 0
                } else {
                    currency + value.roundToInt().toString()
                }
            }
            return numberString
        }

        init {
            this.mFormat = format
        }
    }

    class IntValueFormatEmpty(format: DecimalFormat?) : PercentFormatter() {

        override fun getFormattedValue(value: Float): String {
            return value.roundToInt().toString()
        }

        init {
            this.mFormat = format
        }
    }

}
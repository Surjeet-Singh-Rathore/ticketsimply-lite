package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bitla.ts.domain.pojo.booking.StageData
import com.bitla.ts.utils.common.inputFormatToOutput
import com.bitla.ts.utils.constants.DATE_FORMAT_D_M_Y
import com.bitla.ts.utils.constants.DATE_FORMAT_MMMM_DD_EEEE_YYYY
import com.bitla.ts.utils.constants.DATE_FORMAT_MMM_DD_EEE_YYYY
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Created by Surjeet Rathore on 02/08/21.
 */

abstract class BaseViewModel : ViewModel() {
    companion object {
        val tag: String = BaseViewModel::class.java.simpleName
    }

    private val dates = MutableLiveData<MutableList<StageData>>()
    val listOfDates: LiveData<MutableList<StageData>>
        get() = dates

    private val datesFiveDays = MutableLiveData<MutableList<StageData>>()
    val listOfDatesFiveDays: LiveData<MutableList<StageData>>
        get() = datesFiveDays

    fun getNextCalenderDates(date: String, travelDate: String) {
        val dateObjArrayList = mutableListOf<StageData>()
        try {
            val calendar: Calendar = Calendar.getInstance()
            val arrayOfData = date.split("-").map { it.toInt() }
            calendar.set(arrayOfData[2], arrayOfData[1].minus(1), arrayOfData[0])
            var i = 0
            val days = 7
            val updatedFormatter = SimpleDateFormat(DATE_FORMAT_MMMM_DD_EEEE_YYYY, Locale.getDefault())
            val formatter = SimpleDateFormat(DATE_FORMAT_MMM_DD_EEE_YYYY, Locale.getDefault())
            while (i < days) {
                i++
                if(PreferenceUtils.getlang() == "vi"){
                    dateObjArrayList.add(
                        StageData(
                            updatedFormatter.format(calendar.time),
                            true,
                            false,
                            "DATES"
                        )
                    )
                } else {
                    dateObjArrayList.add(
                        StageData(
                            formatter.format(calendar.time),
                            true,
                            false,
                            "DATES"
                        )
                    )
                }
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }


            if (dateObjArrayList.any {
                    it.title == inputFormatToOutput(
                        travelDate,
                        DATE_FORMAT_D_M_Y, DATE_FORMAT_MMM_DD_EEE_YYYY
                    )
                }) {
                val selectedDate = StageData(
                    "${
                        inputFormatToOutput(
                            travelDate,
                            DATE_FORMAT_D_M_Y, DATE_FORMAT_MMM_DD_EEE_YYYY
                        )
                    }", true, true, "DATES"
                )

                dateObjArrayList.forEachIndexed { index, stageData ->
                    if (selectedDate.title == stageData.title) {
                        dateObjArrayList[index] = selectedDate
                    }
                }
            }
            /*else {
            val selectedDate = StageData(dateObjArrayList[0].title, true, true, "DATES")
            dateObjArrayList[0] = selectedDate
        }*/

        }finally {
            dates.postValue(dateObjArrayList)
        }

    }

    fun getNextFiveCalenderDates(date: String, travelDate: String) {
        val dateObjArrayList = mutableListOf<StageData>()

        val calendar: Calendar = Calendar.getInstance()
        val arrayOfData = date.split("-").map { it.toInt() }
        calendar.set(arrayOfData[2], arrayOfData[1].minus(1), arrayOfData[0])

        var i = 0
        val days = 5

        val formatter = SimpleDateFormat(DATE_FORMAT_MMM_DD_EEE_YYYY, Locale.getDefault())

        while (i < days) {
            i++

            dateObjArrayList.add(
                StageData(
                    formatter.format(calendar.time),
                    true, false, "DATES"
                )
            )
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        if (dateObjArrayList.any {
                it.title == inputFormatToOutput(
                    travelDate,
                    DATE_FORMAT_D_M_Y, DATE_FORMAT_MMM_DD_EEE_YYYY
                )
            }) {

            val selectedDate = StageData(
                inputFormatToOutput(
                    travelDate,
                    DATE_FORMAT_D_M_Y, DATE_FORMAT_MMM_DD_EEE_YYYY
                ), true, true, "DATES"
            )

            dateObjArrayList.forEachIndexed { index, stageData ->
                if (selectedDate.title == stageData.title) {
                    dateObjArrayList[index] = selectedDate
                }
            }
        }
        /*else {
            val selectedDate = StageData(dateObjArrayList[0].title, true, true, "DATES")
            dateObjArrayList[0] = selectedDate
        }*/
        datesFiveDays.postValue(dateObjArrayList)
    }
}
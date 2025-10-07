package com.example.buscoach

import com.example.buscoach.service_details_response.SeatDetail
import com.example.buscoach.service_details_response.ServiceDetailsModel
import com.example.buscoach.utils.Const.Companion.SELECTED
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class CoachRepository {
    suspend fun getSelectedSeats(
        isAllSeatsSelection: Boolean,
        seatDetails: List<SeatDetail>,
        selectedSeats: MutableList<String>
    ): String {
        return coroutineScope {
            async {
                if (isAllSeatsSelection) {
                    seatDetails.forEach {
                        if (!it.number.isNullOrEmpty()) {
                            selectedSeats.add(it.number ?: "")
                        }
                    }
                }
                selectedSeats.joinToString(",")
            }
        }.await()
    }

    suspend fun getSeatSelectionColor(
        serviceDetailsModel: ServiceDetailsModel
    ): String {
        var selectedColor = ""
        return coroutineScope {
            async {
                serviceDetailsModel.body?.legendDetails?.forEach {
                    if (it.colorLegend.toString() == SELECTED)
                        selectedColor = it.color ?: ""
                }
                return@async selectedColor
            }
        }.await()
    }
}

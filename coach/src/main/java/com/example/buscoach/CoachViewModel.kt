package com.example.buscoach

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buscoach.service_details_response.SeatDetail
import com.example.buscoach.service_details_response.ServiceDetailsModel
import com.example.buscoach.utils.Const.Companion.BERTH
import com.example.buscoach.utils.Const.Companion.HORIZONTAL_SLEEPER
import com.example.buscoach.utils.Const.Companion.IMAGE_ICON
import com.example.buscoach.utils.Const.Companion.SEATER
import com.example.buscoach.utils.Const.Companion.SEMI_SLEEPER
import com.example.buscoach.utils.Const.Companion.VERTICAL_SLEEPER
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CoachViewModel : ViewModel() {
    private var coachRepository = CoachRepository()
    var selectedSeat : MutableLiveData<String> = MutableLiveData()
    val selectedSeats = mutableListOf<String>()
    var currentPNR: MutableLiveData<String> = MutableLiveData()
    var currentSeatDetail: MutableLiveData<SeatDetail> = MutableLiveData()

    private val _selectedSeatColor = MutableLiveData<String>()
    val selectedSeatColor: LiveData<String>
        get() = _selectedSeatColor


    fun getSelectedSeats(isAllSeatsSelection : Boolean, seatDetails : List<SeatDetail>)
    {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main){
                selectedSeat.postValue(coachRepository.getSelectedSeats(isAllSeatsSelection,seatDetails,selectedSeats))
            }
        }
    }

    fun getSelectedSeatColor(serviceDetailsModel: ServiceDetailsModel)
    {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main){
                _selectedSeatColor.postValue(coachRepository.getSeatSelectionColor(serviceDetailsModel))
            }

        }
    }

     fun checkSeatType(seatDetail: SeatDetail): String {
        return if (seatDetail.number?.contains("IMG",true) == false && seatDetail.isHorizontal == false && seatDetail.type?.contains(BERTH,true) == true)
            VERTICAL_SLEEPER
        else if (seatDetail.number?.contains("IMG",true) == false && seatDetail.isBerth == false && seatDetail.isHorizontal == false && (seatDetail.type == SEMI_SLEEPER || seatDetail.type == SEATER))
            SEATER
        else if (seatDetail.number?.contains("IMG",true) == false && seatDetail.isHorizontal == true)
            HORIZONTAL_SLEEPER
        else if (seatDetail.number?.contains("IMG",true) == true)
            IMAGE_ICON
        else
            ""
    }


}
package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitla.ts.domain.pojo.boarding_stage_seats.request.BoardingStageSeatsRequest
import com.bitla.ts.domain.pojo.reservation_stages.request.ReservationStagesRequest
import com.bitla.ts.domain.pojo.reservation_stages.response.ReservationStagesResponse
import com.bitla.ts.domain.pojo.service_details_response.ServiceDetailsModel
import com.bitla.ts.domain.repository.CoachLayoutReportingRepository
import com.bitla.ts.koin.networkModule.NetworkProcess
import com.bitla.ts.utils.LoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class CoachLayoutReportingViewModel(private val coachLayoutReportingRepository: CoachLayoutReportingRepository) :
    ViewModel() {

    private val _reservationStagesResponse = MutableLiveData<ReservationStagesResponse>()
    val reservationStagesResponse: LiveData<ReservationStagesResponse> get() = _reservationStagesResponse

    private val _boardingStagesSeatsResponse = MutableLiveData<ServiceDetailsModel>()
    val boardingStagesSeatsResponse: LiveData<ServiceDetailsModel> get() = _boardingStagesSeatsResponse

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    val messageSharedFlow = MutableSharedFlow<String>()

    fun getReservationStagesApi(
        reservationStagesRequest: ReservationStagesRequest
    ) {

        viewModelScope.launch(Dispatchers.IO) {
            coachLayoutReportingRepository.getReservationStagesApi(
                reservationStagesRequest
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _reservationStagesResponse.postValue(
                            it.data
                        )
                    }

                    is NetworkProcess.Failure -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        messageSharedFlow.emit(it.message)
                    }
                }
            }

        }
    }

    fun getBoardingStageSeatsApi(
        boardingStagesSeatsRequest: BoardingStageSeatsRequest
    ) {

        viewModelScope.launch(Dispatchers.IO) {
            coachLayoutReportingRepository.getBoardingStageSeatsApi(
                boardingStagesSeatsRequest
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _boardingStagesSeatsResponse.postValue(
                            it.data
                        )
                    }

                    is NetworkProcess.Failure -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        messageSharedFlow.emit(it.message)
                    }
                }
            }

        }
    }
}
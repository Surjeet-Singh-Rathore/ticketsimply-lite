package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bitla.ts.domain.pojo.auto_shift.AutoShiftResponse
import com.bitla.ts.domain.pojo.multiple_shift_passenger.MultiShiftPassengerResponse
import com.bitla.ts.domain.pojo.singleShiftPassenger.SingleShiftPassengerResponse
import com.bitla.ts.domain.pojo.singleShiftPassenger.request.ReqBody
import com.bitla.ts.domain.repository.ShiftPassengerRepository
import com.bitla.ts.koin.networkModule.NetworkProcess
import com.bitla.ts.utils.LoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch


class ShiftPassengerViewModel<T : Any?>(private val shiftPassengerRepository: ShiftPassengerRepository) :
    BaseViewModel() {
    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState


    private val _singleShiftPassengerResponse = MutableLiveData<SingleShiftPassengerResponse>()
    val singleShiftPassengerResponse: LiveData<SingleShiftPassengerResponse>
        get() = _singleShiftPassengerResponse


    private val _multiShiftPassengerResponse = MutableLiveData<MultiShiftPassengerResponse>()
    val multiShiftPassengerResponse: LiveData<MultiShiftPassengerResponse>
        get() = _multiShiftPassengerResponse


    private val _autoShiftResponse = MutableLiveData<AutoShiftResponse>()
    val autoShiftResponse: LiveData<AutoShiftResponse>
        get() = _autoShiftResponse


    private var apiType: String? = null
    val messageSharedFlow = MutableSharedFlow<String>()



    /*fun singleShiftPassengerAPI(
        authorization: String,
        apiKey: String,
        singleShiftPassengerRequest: SingleShiftPassengerRequest,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            _singleShiftPassengerResponse.postValue(
                shiftPassengerRepository.singleShiftPassenger(
                    authorization,
                    apiKey,
                    singleShiftPassengerRequest
                ).body()
            )
        }
    }*/

    fun singleShiftPassengerAPI(
        singleShiftPassengerRequest: ReqBody,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            shiftPassengerRepository.newSingleShiftPassenger(
                singleShiftPassengerRequest
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _singleShiftPassengerResponse .postValue(
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

    /*fun multiShiftPassengerAPI(
        authorization: String,
        apiKey: String,
        multiShiftPassengerRequest: MultiShiftPassengerRequest,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            _multiShiftPassengerResponse.postValue(
                shiftPassengerRepository.multiShiftPassenger(
                    authorization,
                    apiKey,
                    multiShiftPassengerRequest
                ).body()
            )
        }
    }  */

    fun multiShiftPassengerAPI(
        multiShiftPassengerRequest: com.bitla.ts.domain.pojo.multiple_shift_passenger.request.ReqBody,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            shiftPassengerRepository.newMultiShiftPassenger(
                multiShiftPassengerRequest
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _multiShiftPassengerResponse.postValue(
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


    /*fun autoShiftAPI(
        authorization: String,
        apiKey: String,
        autoShiftRequest: AutoShiftRequest,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            _autoShiftResponse.postValue(
                shiftPassengerRepository.autoShiftPassenger(
                    authorization,
                    apiKey,
                    autoShiftRequest
                ).body()
            )
        }
    }*/

    fun autoShiftAPI(
        autoShiftRequest: com.bitla.ts.domain.pojo.auto_shift.request.ReqBody,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            shiftPassengerRepository.newAutoShiftPassenger(
                autoShiftRequest
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _autoShiftResponse.postValue(
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
package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.*
import com.bitla.ts.domain.pojo.city_details.response.*
import com.bitla.ts.domain.pojo.multistation_data.*
import com.bitla.ts.domain.pojo.phone_block_temp_to_permanent_data.request.*
import com.bitla.ts.domain.pojo.phone_block_temp_to_permanent_data.response.*
import com.bitla.ts.domain.pojo.state_details.response.*
import com.bitla.ts.domain.repository.*
import com.bitla.ts.koin.networkModule.NetworkProcess
import com.bitla.ts.utils.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow

class CityDetailViewModel<T : Any?>(private val cityDetailRepository: CityDetailRepository) :
    ViewModel() {


    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    private val _cityDetailResponse = MutableLiveData<CityDetailsResponseModel>()
    val cityDetailResponse: LiveData<CityDetailsResponseModel>
        get() = _cityDetailResponse

    private val _stateDetailResponse = MutableLiveData<StateDetailsResponseModel>()
    val stateDetailResponse: LiveData<StateDetailsResponseModel>
        get() = _stateDetailResponse

    private val _multistationSeatData = MutableLiveData<MultistationRespBody>()
    val multistationSeatData: LiveData<MultistationRespBody>
        get() = _multistationSeatData

    private val _phoneBlockTempToPermanentResponse =
        MutableLiveData<PhoneBlockTempToPermanentResponse>()

    val phoneBlockTempToPermanent: LiveData<PhoneBlockTempToPermanentResponse>
        get() = _phoneBlockTempToPermanentResponse


    private var apiType: String? = null

    val messageSharedFlow = MutableSharedFlow<String>()


    /*fun cityDetailAPI(
        authorization: String,
        apiKey: String,
        cityDetailRequest: CityDetailRequest,
        apiType: String,
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            _cityDetailResponse.postValue(
                cityDetailRepository.cityDetailservice(
                    authorization,
                    apiKey,
                    cityDetailRequest
                ).body()
            )
        }
    }*/

    fun cityDetailAPI(
        apiKey: String,
        responseFormat: String,
        locale: String,
        apiType: String,
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {

            cityDetailRepository.newCityDetailservice(
                apiKey,
                responseFormat,
                locale,
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _cityDetailResponse.postValue(
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

    fun multistationPassengerDataApi(
        apiKey: String,
        reservationId: String,
        seatNumber: String,
        isBima: Boolean,
        apiType: String,
        locale: String,
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            cityDetailRepository.getMultiStationSeatDataApi(
                apiKey = apiKey,
                reservationId = reservationId,
                seatNumber = seatNumber,
                isBima = isBima,
                locale = locale
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _multistationSeatData.postValue(
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

    fun phoneBlockTempToPermanent(
        phoneBlockTempToPermanent: PhoneBlockTempToPermanentReq,
        apiType: String,
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            cityDetailRepository.getPhoneBlockTempToPermanent(
                phoneBlockTempToPermanent
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _phoneBlockTempToPermanentResponse.postValue(
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

    /*fun cityDetailAPI(
        apiKey: String,
        responseFormat : String,
        locale: String,
        apiType: String,
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            _cityDetailResponse.postValue(
                cityDetailRepository.newCityDetailservice(
                    apiKey,
                    responseFormat,
                    locale,
                ).body()
            )
        }
    }*/


    /*fun stateDetailAPI(
        authorization: String,
        apiKey: String,
        stateDetailRequest: StateDetailRequest,
        apiType: String,
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            _stateDetailResponse.postValue(
                cityDetailRepository.stateDetailService(
                    authorization,
                    apiKey,
                    stateDetailRequest
                ).body()
            )
        }
    }*/

    fun stateDetailAPI(
        apiKey: String,
        responseFormat: String,
        locale: String,
        apiType: String,
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            cityDetailRepository.newStateDetailService(
                apiKey,
                responseFormat,
                locale
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _stateDetailResponse.postValue(
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
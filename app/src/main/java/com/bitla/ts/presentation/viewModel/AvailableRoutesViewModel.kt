package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bitla.ts.domain.pojo.available_routes.AvailableRoutesModel
import com.bitla.ts.domain.pojo.bp_dp_details.BpDpDetails
import com.bitla.ts.domain.pojo.service_routes_list.response.ServiceRoutesListResponse
import com.bitla.ts.domain.pojo.single_block_unblock.SingleBlockUnblock
import com.bitla.ts.domain.pojo.single_block_unblock.single_block_unblock_request.SingleBlockUnblockRequest
import com.bitla.ts.domain.repository.AvailableRoutesRepository
import com.bitla.ts.koin.networkModule.NetworkProcess
import com.bitla.ts.utils.LoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class AvailableRoutesViewModel<T : Any?>(private val availableRoutesRepository: AvailableRoutesRepository) :
    BaseViewModel(){

    companion object {
        val TAG: String = AvailableRoutesViewModel::class.java.simpleName
    }

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState


    private val _dataAvailableRoutes = MutableLiveData<AvailableRoutesModel>()
    val dataAvailableRoutes: LiveData<AvailableRoutesModel>
        get() = _dataAvailableRoutes


    private val _serviceRoutesList = MutableLiveData<ServiceRoutesListResponse>()
    val serviceRoutesList: LiveData<ServiceRoutesListResponse>
        get() = _serviceRoutesList

    private val _dataSingleBLockUnblock = MutableLiveData<SingleBlockUnblock>()
    val dataSingleBLockUnblock: LiveData<SingleBlockUnblock>
        get() = _dataSingleBLockUnblock

    private val _bpDpDetails = MutableLiveData<BpDpDetails>()
    val bpDpDetails: LiveData<BpDpDetails>
        get() = _bpDpDetails

    private var apiType: String? = null

    val messageSharedFlow = MutableSharedFlow<String>()



    /*   fun availableRoutesApi(
           authorization: String,
           apiKey: String,
           availableRoutesRequest: AvailableRoutesRequest,
           apiType: String
       ) {

           _loadingState.postValue(LoadingState.LOADING)

           viewModelScope.launch(Dispatchers.IO) {
               _dataAvailableRoutes.postValue(
                   availableRoutesRepository.getAvailableRoutes(
                       authorization,
                       apiKey,
                       availableRoutesRequest
                   ).body()
               )
           }
       }  */

    fun availableRoutesApi(
        apiKey: String,
        originId: String,
        destinationId: String,
        showInJourneyServices: String,
        isCsShared: Boolean,
        operatorkey: String,
        responseFormat: String,
        travelDate: String,
        showOnlyAvalServices: String,
        locale: String,
        apiType: String,
        appBimaEnabled: Boolean,
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
                availableRoutesRepository.getNewAvailableRoutes(
                    originId = originId,
                    destinationId = destinationId,
                    travelDate = travelDate,
                    apiKey = apiKey,
                    showInJourneyServices = showInJourneyServices,
                    isCsShared = isCsShared,
                    operatorkey = operatorkey,
                    responseFormat = responseFormat,
                    showOnlyAvalServices = showOnlyAvalServices,
                    locale = locale,
                    app_bima_enabled = appBimaEnabled
                ).collect {
                    when (it) {
                        is NetworkProcess.Loading -> {}
                        is NetworkProcess.Success -> {
                            _loadingState.postValue(LoadingState.LOADED)
                            _dataAvailableRoutes.postValue(it.data)
                        }

                        is NetworkProcess.Failure -> {
                            _loadingState.postValue(LoadingState.LOADED)
                            messageSharedFlow.emit(it.message)
                        }
                    }
                }

        }
    }

    fun availableRoutesForAgent(
        apiKey: String,
        originId: String,
        destinationId: String,
        showInJourneyServices: String,
        isCsShared: Boolean,
        operatorkey: String,
        responseFormat: String,
        travelDate: String,
        showOnlyAvalServices: String,
        locale: String,
        pagination: String? = null, per_page: String? = null, page: String? = null,
    ) {
        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {

                availableRoutesRepository.availableRoutesForAgent(
                    originId,
                    destinationId,
                    travelDate,
                    apiKey,
                    showInJourneyServices,
                    isCsShared,
                    operatorkey,
                    responseFormat,
                    showOnlyAvalServices,
                    locale, pagination, per_page, page
                ).collect {
                    when (it) {
                        is NetworkProcess.Loading -> {}
                        is NetworkProcess.Success -> {
                            _loadingState.postValue(LoadingState.LOADED)
                            _dataAvailableRoutes.postValue(it.data)
                        }
                        is NetworkProcess.Failure -> {
                            _loadingState.postValue(LoadingState.LOADED)
                            messageSharedFlow.emit(it.message)
                        }
                    }
                }
        }
    }

    fun serviceRoutesListApi(
        apiKey: String,
        originId: String,
        destinationId: String,
        showInJourneyServices: String,
        isCsShared: Boolean,
        operatorkey: String,
        responseFormat: String,
        travelDate: String,
        showOnlyAvalServices: String,
        locale: String,
        apiType: String,
    ) {
        this.apiType = apiType
        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {

                availableRoutesRepository.getServiceRoutesList(
                    originId = originId,
                    destinationId = destinationId,
                    travelDate = travelDate,
                    apiKey = apiKey,
                    showInJourneyServices = showInJourneyServices,
                    isCsShared = isCsShared,
                    operatorkey = operatorkey,
                    responseFormat = responseFormat,
                    showOnlyAvalServices = showOnlyAvalServices,
                    locale = locale
                ).collect {
                    when (it) {
                        is NetworkProcess.Loading -> {}
                        is NetworkProcess.Success -> {
                            _loadingState.postValue(LoadingState.LOADED)
                            _serviceRoutesList.postValue(it.data)
                        }

                        is NetworkProcess.Failure -> {
                            _loadingState.postValue(LoadingState.LOADED)
                            messageSharedFlow.emit(it.message)
                        }
                    }
                }

        }
    }

    fun getBpDpDetails(
        apiKey: String,
        originId: String,
        destinationId: String,
        resId: String,
    ) {
        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            availableRoutesRepository.getBpDpDetails(
                apiKey,
                originId,
                destinationId,
                resId
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _bpDpDetails.postValue(
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

    fun singleBlockUnblockApi(
        authorization: String,
        apiKey: String,
        singleBlockUnblockRequest: SingleBlockUnblockRequest,
        apiType: String,
        authPin: String,
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            availableRoutesRepository.singleBlockUnblock(
                authorization,
                apiKey,
                singleBlockUnblockRequest,
                authPin
            ) .collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _dataSingleBLockUnblock .postValue(
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
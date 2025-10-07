package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bitla.ts.domain.pojo.add_rate_card.createRateCard.request.CreateRateCardReqBody
import com.bitla.ts.domain.pojo.add_rate_card.createRateCard.response.CreateRateCardResponse
import com.bitla.ts.domain.pojo.add_rate_card.deleteRateCard.request.DeleteRateCardReqBody
import com.bitla.ts.domain.pojo.add_rate_card.deleteRateCard.response.DeleteRateCardResponse
import com.bitla.ts.domain.pojo.add_rate_card.editRateCard.request.EditRateCardReqBody
import com.bitla.ts.domain.pojo.add_rate_card.editRateCard.response.EditRateCardResponse
import com.bitla.ts.domain.pojo.add_rate_card.fetchRouteWiseFareDetails.request.FetchRouteWiseFareReqBody
import com.bitla.ts.domain.pojo.add_rate_card.fetchRouteWiseFareDetails.response.FetchRouteWiseFareResponse
import com.bitla.ts.domain.pojo.add_rate_card.fetchShowRateCard.request.FetchShowRateCardReqBody
import com.bitla.ts.domain.pojo.add_rate_card.fetchShowRateCard.response.FetchShowRateCardResponse
import com.bitla.ts.domain.pojo.add_rate_card.viewRateCard.request.ViewRateCardReqBody
import com.bitla.ts.domain.pojo.add_rate_card.viewRateCard.response.ViewRateCardResponse
import com.bitla.ts.domain.repository.AddRateCardRepository
import com.bitla.ts.koin.networkModule.NetworkProcess
import com.bitla.ts.utils.LoadingState
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class AddRateCardViewModel<T : Any?>(private val addRateCardRepository: AddRateCardRepository) :
    BaseViewModel() {


//    private val _loadingState = MutableLiveData<LoadingState>()
//    val loadingState: LiveData<LoadingState> get() = _loadingState


    private val _fetchShowRateCardResponse = MutableLiveData<FetchShowRateCardResponse>()
    val fetchShowRateCardResponse: LiveData<FetchShowRateCardResponse>
        get() = _fetchShowRateCardResponse

    private val _fetchRouteWiseFareResponse = MutableLiveData<FetchRouteWiseFareResponse>()
    val fetchRouteWiseFareResponse: LiveData<FetchRouteWiseFareResponse>
        get() = _fetchRouteWiseFareResponse

    private val _viewRateCardResponse = MutableLiveData<ViewRateCardResponse>()
    val viewRateCardResponse: LiveData<ViewRateCardResponse>
        get() = _viewRateCardResponse

    private val _deleteRateCardResponse = MutableLiveData<DeleteRateCardResponse>()
    val deleteRateCardResponse: LiveData<DeleteRateCardResponse>
        get() = _deleteRateCardResponse

    private val _createRateCardResponse = MutableLiveData<CreateRateCardResponse>()
    val createRateCardResponse: LiveData<CreateRateCardResponse>
        get() = _createRateCardResponse

    private val _editRateCardResponse = MutableLiveData<EditRateCardResponse>()
    val editRateCardResponse: LiveData<EditRateCardResponse>
        get() = _editRateCardResponse

    val messageSharedFlow = MutableSharedFlow<String>()


    fun fetchShowRateCardApi(
        fetchShowRateCardReqBody: FetchShowRateCardReqBody,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            addRateCardRepository.fetchShowRateCardApiService(
                fetchShowRateCardReqBody
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _fetchShowRateCardResponse .postValue(
                            it.data
                        )
                }

                is NetworkProcess.Failure -> {
                    messageSharedFlow.emit(it.message)
                }
            }
        }
        }
    }

    fun fetchRouteWiseFareDetailsApi(
        reqBodyFetchRouteWiseFare: FetchRouteWiseFareReqBody,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            addRateCardRepository.fetchRouteWiseFareApiService(
                reqBodyFetchRouteWiseFare
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _fetchRouteWiseFareResponse .postValue(
                            it.data
                        )
                }

                is NetworkProcess.Failure -> {
                    messageSharedFlow.emit(it.message)
                }
            }
        }
        }
    }

    fun createRateCardApi(
        reqBodyCreateRateCard: CreateRateCardReqBody,
    ) {

        viewModelScope.launch(Dispatchers.IO) {
            addRateCardRepository.createRateCardApiService(
                reqBodyCreateRateCard
            ) .collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _createRateCardResponse.postValue(
                            it.data
                        )
                }

                is NetworkProcess.Failure -> {
                    messageSharedFlow.emit(it.message)
                }
            }
        }

        }
        val gson = GsonBuilder().disableHtmlEscaping().create()
        val json = gson.toJson(reqBodyCreateRateCard)
        Timber.d("addRateCardRepository", "createRateCardApi: " + json.toString())
    }

    fun editRateCardApi(
        reqBodyEditRateCard: EditRateCardReqBody,
    ) {

        viewModelScope.launch(Dispatchers.IO) {
            addRateCardRepository.editRateCardApiService(
                reqBodyEditRateCard
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _editRateCardResponse.postValue(
                            it.data
                        )
                }

                is NetworkProcess.Failure -> {
                    messageSharedFlow.emit(it.message)
                }
            }
        }

        }
        val gson = GsonBuilder().disableHtmlEscaping().create()
        val json = gson.toJson(reqBodyEditRateCard)
        Timber.d("addRateCardRepository", "editRateCardApi: " + json.toString())
    }

    fun viewRateCardApi(
        reqBodyViewRateCard: ViewRateCardReqBody,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            addRateCardRepository.viewRateCardApiService(
                reqBodyViewRateCard
            ) .collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _viewRateCardResponse .postValue(
                            it.data
                        )
                }

                is NetworkProcess.Failure -> {
                    messageSharedFlow.emit(it.message)
                }
            }
        }
        }
    }

    fun deleteRateCardApi(
        reqBodyDeleteRateCard: DeleteRateCardReqBody,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            addRateCardRepository.deleteRateCardApiService(
                reqBodyDeleteRateCard
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _deleteRateCardResponse.postValue(
                            it.data
                        )
                }

                is NetworkProcess.Failure -> {
                    messageSharedFlow.emit(it.message)
                }
            }
        }
        }
    }


//    fun addRateCardFareApi(
//        updateRateCardFareRequest: com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_fare.request.ReqBody,
//        apiType: String,
//    ) {
//
//        _loadingState.postValue(LoadingState.LOADING)
//
//        viewModelScope.launch(Dispatchers.IO) {
//            _updateRateCardFareResponse.postValue(
//                pickUpRepository.newUpdateRateCardFareService(
//                    updateRateCardFareRequest
//                ).body()
//            )
//        }
//    }
//
//    fun viewRateCardApi(
//        multiStationWiseFareRequest: com.bitla.ts.domain.pojo.update_rate_card.multistation_wise_fare.request.ReqBody,
//        apiType: String,
//    ) {
//
//        _loadingState.postValue(LoadingState.LOADING)
//
//        viewModelScope.launch(Dispatchers.IO) {
//            _fetchMultiStatioWiseFareResponse.postValue(
//                pickUpRepository.newFetchMultiStatioWiseFareService(
//                    multiStationWiseFareRequest
//                ).body()
//            )
//        }
//        val gson = GsonBuilder().disableHtmlEscaping().create()
//        val json = gson.toJson(multiStationWiseFareRequest)
//        Timber.d("PickUpChartViewModel", "multiStationWiseFareRequest: " + json.toString())
//    }
//
//
//    fun createRateCardFareApi(
//        manageFareMultiStationRequest: com.bitla.ts.domain.pojo.update_rate_card.manage_fare_multistaion.request.ReqBody,
//        apiType: String,
//    ) {
//
//        _loadingState.postValue(LoadingState.LOADING)
//
//        viewModelScope.launch(Dispatchers.IO) {
//            _manageMultiStatioWiseFareApi.postValue(
//                pickUpRepository.newManageMultiStatioWiseFareApi(
//                    manageFareMultiStationRequest
//                ).body()
//            )
//        }
//        val gson = GsonBuilder().disableHtmlEscaping().create()
//        val json = gson.toJson(manageFareMultiStationRequest)
//        Timber.d("PickUpChartViewModel", "manageFareMultiStationApi: " + json.toString())
//    }


}
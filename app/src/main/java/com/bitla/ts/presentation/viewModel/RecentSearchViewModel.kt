package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bitla.ts.domain.pojo.destination_list.DestinationList
import com.bitla.ts.domain.pojo.destination_pair.DestinationPairModel
import com.bitla.ts.domain.pojo.recent_bookings.RecentBookings
import com.bitla.ts.domain.repository.RecentSearchRepository
import com.bitla.ts.koin.networkModule.NetworkProcess
import com.bitla.ts.utils.LoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class RecentSearchViewModel<T : Any?>(private val recentSearchRepository: RecentSearchRepository) :
    BaseViewModel() {

    companion object {
        val TAG: String = RecentSearchViewModel::class.java.simpleName
    }

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState


    private val _destinationList = MutableLiveData<DestinationPairModel>()
    val dataDestinationPairList: LiveData<DestinationPairModel>
        get() = _destinationList

    private val _destinationListWithOrigin = MutableLiveData<DestinationList>()
    val destinationListWithOrigin: LiveData<DestinationList>
        get() = _destinationListWithOrigin

    private val _callDestinationPairApi = MutableLiveData(true)
    val callDestinationPairApi: LiveData<Boolean>
        get() = _callDestinationPairApi

    private val _recentBookings = MutableLiveData<RecentBookings>()
    val recentBookings: LiveData<RecentBookings>
        get() = _recentBookings

    private var apiType: String? = null

    val messageSharedFlow = MutableSharedFlow<String>()



    /* fun getDestinationPair(
         authorization: String,
         apiKey: String,
         availableRoutesRequest: DestinationPairRequest,
         apiType: String
     ) {

         _loadingState.postValue(LoadingState.LOADING)

         viewModelScope.launch(Dispatchers.IO) {
             _destinationList.postValue(
                 recentSearchRepository.getDestinationPair(
                     authorization,
                     apiKey,
                     availableRoutesRequest
                 ).body()
             )
         }
     }*/

    fun getDestinationPair(
        apiKey: String,
        operatorKey: String,
        responseFormat: String,
        appBimaEnabled: Boolean,
        locale: String,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            recentSearchRepository.getNewDestinationPair(
                apiKey = apiKey,
                operatorKey = operatorKey,
                responseFormat = responseFormat,
                appBimaEnable = appBimaEnabled,
                locale = locale
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _destinationList.postValue(
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

    fun destinationListWithOrigin(
        apiKey: String,
        originId: String,
        resId: String? = null
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            recentSearchRepository.destinationListWithOrigin(
                apiKey,
                originId,
                resId
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _destinationListWithOrigin.postValue(
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

    fun setDestinationPairApiCall(callApi: Boolean) {
        _callDestinationPairApi.postValue(callApi)
    }


    fun getDestinationList(
        apiKey: String,
        originId: String,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _destinationListWithOrigin.postValue(
                recentSearchRepository.getDestinationList(
                    apiKey,
                    originId,
                ).body()
            )
        }
    }


}


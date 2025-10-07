package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitla.ts.domain.pojo.passenger_history.PassengersHistory
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.repository.PassengerHistoryRepository
import com.bitla.ts.koin.networkModule.NetworkProcess
import com.bitla.ts.utils.LoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class PassengerHistoryViewModel<T : Any?>(private val passengerHistoryRepository: PassengerHistoryRepository) :
    ViewModel() {

    companion object {
        val TAG = PassengerHistoryViewModel::class.java.simpleName
    }

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    private val _dataPassengerHistory = MutableLiveData<PassengersHistory>()
    val dataPassengersHistory: LiveData<PassengersHistory>
        get() = _dataPassengerHistory


    private var apiType: String? = null

    val messageSharedFlow = MutableSharedFlow<String>()

    /* fun passengerHistoryApi(
         authorization: String,
         apiKey: String,
         passengerHistoryRequest: PassengerHistoryRequest,
         apiType: String
     ) {

         _loadingState.postValue(LoadingState.LOADING)

         viewModelScope.launch(Dispatchers.IO) {
             _dataPassengerHistory.postValue(
                 passengerHistoryRepository.passengerHistory(
                     authorization,
                     apiKey,
                     passengerHistoryRequest
                 ).body()
             )
         }
     }  */

    var privilegesLiveData = MutableLiveData<PrivilegeResponseModel?>()

    fun updatePrivileges(privileges: PrivilegeResponseModel?) {
        privilegesLiveData.value = privileges
    }


    fun passengerHistoryApi(
        apiKey: String,
        response_format: String,
        passenger_details: String,
        operator_api_key: String,
        locale: String,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            passengerHistoryRepository.newPassengerHistory(
                apiKey,passenger_details,operator_api_key,locale
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _dataPassengerHistory.postValue(
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
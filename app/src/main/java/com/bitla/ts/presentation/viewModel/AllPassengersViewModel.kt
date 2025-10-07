package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bitla.ts.domain.pojo.notify_passengers.NotifyPassengersModel
import com.bitla.ts.domain.repository.AllPassengersRepository
import com.bitla.ts.koin.networkModule.NetworkProcess
import com.bitla.ts.utils.LoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch


class AllPassengersViewModel<T : Any?>(private val allPassengersRepository: AllPassengersRepository) :
    BaseViewModel() {

    companion object {
        val tag: String = AllPassengersViewModel::class.java.simpleName
    }

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    private val _notifyPassengers = MutableLiveData<NotifyPassengersModel>()
    val notifyPassengers: LiveData<NotifyPassengersModel>
        get() = _notifyPassengers

    private val _etOnChange = MutableLiveData<Boolean>()
    val etOnChange: LiveData<Boolean>
        get() = _etOnChange

    private var apiType: String? = null

    val messageSharedFlow = MutableSharedFlow<String>()

    fun etTextWatcher(text: String) {
        if (text.isNotEmpty())
            _etOnChange.postValue(true)
        else
            _etOnChange.postValue(false)
    }

    fun checkboxWatcher(checkBox: Boolean) {
        if (checkBox)
            _etOnChange.postValue(true)
        else
            _etOnChange.postValue(false)
    }

   /* fun notifyPassengersApi(
        authorization: String,
        apiKey: String,
        notifyPassengersRequest: NotifyPassengersRequest,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            _notifyPassengers.postValue(
                allPassengersRepository.notifyPassengers(
                    authorization,
                    apiKey,
                    notifyPassengersRequest
                ).body()
            )
        }
    }
    */

    fun notifyPassengersApi(
        notifyPassengersRequest: com.bitla.ts.domain.pojo.notify_passengers.request.ReqBody,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            allPassengersRepository.newNotifyPassengers(
                notifyPassengersRequest
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _notifyPassengers.postValue(
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
package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bitla.ts.domain.pojo.sms_types.SmsTypesModel
import com.bitla.ts.domain.repository.SmsTypesRepository
import com.bitla.ts.koin.networkModule.NetworkProcess
import com.bitla.ts.utils.LoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch


open class SmsTypesViewModel<T : Any?>(private val smsTypesRepository: SmsTypesRepository) :
    BaseViewModel() {

    companion object {
        val tag: String = SmsTypesViewModel::class.java.simpleName
    }

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    private val _smsTypes = MutableLiveData<SmsTypesModel>()
    val smsTypes: LiveData<SmsTypesModel>
        get() = _smsTypes

    private var apiType: String? = null

    val messageSharedFlow = MutableSharedFlow<String>()

    /* fun smsTypesApi(
         authorization: String,
         apiKey: String,
         smsTypesRequest: SmsTypesRequest,
         apiType: String
     ) {
       
         _loadingState.postValue(LoadingState.LOADING)
         viewModelScope.launch(Dispatchers.IO) {
             _smsTypes.postValue(
                 smsTypesRepository.smsTypes(
                     authorization,
                     apiKey,
                     smsTypesRequest
                 ).body()
             )
         }
     }  */

    fun smsTypesApi(
        apiKey: String,
        resId: String,
        locale: String,
        responseFormat: String,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            smsTypesRepository.newSmsTypes(
                apiKey,
                resId, locale, responseFormat
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)

                        _smsTypes.postValue(
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
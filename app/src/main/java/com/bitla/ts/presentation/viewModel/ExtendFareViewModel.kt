package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitla.ts.domain.pojo.extend_fare.request.RequestBody
import com.bitla.ts.domain.pojo.extend_fare.response.ExtendFareResponse
import com.bitla.ts.domain.repository.ExtendFareRepository
import com.bitla.ts.koin.networkModule.NetworkProcess
import com.bitla.ts.utils.LoadingState
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class ExtendFareViewModel<T : Any?>(private val extendFareRepository: ExtendFareRepository) :
    ViewModel() {

    companion object {
        val TAG: String = ExtendFareViewModel::class.java.simpleName
    }

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    private val _extendFare = MutableLiveData<ExtendFareResponse>()
    val extendFare: LiveData<ExtendFareResponse>
        get() = _extendFare

    private val _validation = MutableLiveData<String>()
    val validationData: LiveData<String>
        get() = _validation

    private val _changeButtonBackground = MutableLiveData<Boolean>()
    val changeButtonBackground: LiveData<Boolean>
        get() = _changeButtonBackground
    private var apiType: String? = null

    val messageSharedFlow = MutableSharedFlow<String>()



    /*fun extendFareApi(
        authorization: String,
        apiKey: String, request: ExtendFareRequestModel, apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)
        val gson = GsonBuilder().disableHtmlEscaping().create()
        val json = gson.toJson(request)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                Thread.sleep(5000)
                _extendFare.postValue(
                    extendFareRepository.extendFare(authorization, apiKey, request).body()
                )
            } catch (e: Exception) {
                _loadingState.postValue(LoadingState.error(e.message))
            }
        }
    }*/

    fun extendFareApi(
        request: RequestBody,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)
        val gson = GsonBuilder().disableHtmlEscaping().create()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Thread.sleep(5000)
                extendFareRepository.newExtendFare(request).collect {
                        when (it) {
                            is NetworkProcess.Loading -> {}
                            is NetworkProcess.Success -> {
                                _loadingState.postValue(LoadingState.LOADED)
                                _extendFare.postValue(
                                        it.data
                                    )
                            }

                            is NetworkProcess.Failure -> {
                                _loadingState.postValue(LoadingState.LOADED)
                                messageSharedFlow.emit(it.message)
                            }
                        }
                    }
            }catch (e: Exception) {
                    _loadingState.postValue(LoadingState.error(e.message))
                }
        }
    }

    fun validation(
        fromDate: String?,
        toDate: String?,
        applyDate: String?,
        performDate: String?,
        fairType: String,
        multipleDates: String,
        isMultipleDates: Boolean
    ) {
        if (fairType.equals("all") && !isMultipleDates) {
            if (fromDate == null || fromDate == "" || fromDate.equals("from date", true)) {
                _validation.postValue("Enter From Date")
            } else if (toDate == null || toDate == "" || toDate.equals("to date", true)) {
                _validation.postValue("Enter To Date")
            } else if (performDate == null || performDate == "" || performDate.equals(
                    "select date",
                    true
                )
            ) {
                _validation.postValue("Enter Select Date")
            } else {
                _validation.postValue("")
            }
        }else if (fairType.equals("all") && isMultipleDates) {
           if ( multipleDates == "" || multipleDates.equals("Select Date", true)) {
                _validation.postValue("Enter Select Date")
            } else if (performDate == null || performDate == "" || performDate.equals(
                    "select date",
                    true
                )
            ) {
                _validation.postValue("Enter Select Date")
            } else {
                _validation.postValue("")
            }
        } else if (fairType.equals("fare") && !isMultipleDates) {
            if (applyDate == null || applyDate == "" || applyDate.equals("apply date", true)) {
                _validation.postValue("Enter Apply Date")
            } else if (performDate == null || performDate == "" || performDate.equals(
                    "select date",
                    true
                )
            ) {
                _validation.postValue("Enter Select Date")
            } else {
                _validation.postValue("")
            }
        }else{
            if ( multipleDates == "" || multipleDates.equals("Select Dates", true)) {
                _validation.postValue("Enter Multiple Date")
            } else if (performDate == null || performDate == "" || performDate.equals(
                    "select date",
                    true
                )
            ) {
                _validation.postValue("Enter Select Date")
            } else {
                _validation.postValue("")
            }
        }
    }

    fun changeButtonBackground(
        fromDate: String?,
        toDate: String?,
        applyDate: String?,
        performDate: String?,
        fairType: String,
        multipleDate:String?,
        isMultipleDates:Boolean
    ) {
        if (fairType.equals("all") && !isMultipleDates) {
            if (fromDate == null || fromDate == "" || fromDate.equals("from date", true)) {
                _changeButtonBackground.postValue(false)
            } else if (toDate == null || toDate == "" || toDate.equals("to date", true)) {
                _changeButtonBackground.postValue(false)
            } else if (performDate == null || performDate == "" || performDate.equals(
                    "select date",
                    true
                )
            ) {
                _changeButtonBackground.postValue(false)
            } else {
                _changeButtonBackground.postValue(true)
            }
        }else if(fairType.equals("all") && isMultipleDates){
           if(multipleDate==null || multipleDate.isEmpty() || multipleDate == "Select dates"){
                _changeButtonBackground.postValue(false)
            }else {
                _changeButtonBackground.postValue(true)
            }
        } else if (fairType.equals("fare") && !isMultipleDates) {
            if (applyDate == null || applyDate == "" || applyDate.equals("apply date", true)) {
                _changeButtonBackground.postValue(false)
            } else if (performDate == null || performDate == "" || performDate.equals(
                    "select date",
                    true
                )
            ) {
                _changeButtonBackground.postValue(false)
            } else {
                _changeButtonBackground.postValue(true)
            }
        }else{
             if(multipleDate==null || multipleDate.isEmpty() || multipleDate == "Select dates"){
                _changeButtonBackground.postValue(false)
            } else {
                _changeButtonBackground.postValue(true)
            }
        }
    }
}
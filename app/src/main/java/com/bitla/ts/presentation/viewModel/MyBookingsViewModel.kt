package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bitla.ts.domain.pojo.my_bookings.response.MyBookings
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.repository.MyBookingsRepository
import com.bitla.ts.koin.networkModule.NetworkProcess
import com.bitla.ts.utils.LoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class MyBookingsViewModel<T : Any?>(private val myBookingsRepository: MyBookingsRepository) :
    BaseViewModel(){

    companion object {
        val tag: String = MyBookingsViewModel::class.java.simpleName
    }

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    private val _dataMyBookings = MutableLiveData<MyBookings>()
    val dataMyBookings: LiveData<MyBookings>
        get() = _dataMyBookings


    private var apiType: String? = null

    val messageSharedFlow = MutableSharedFlow<String>()


    /* fun myBookingsApi(
         authorization: String,
         apiKey: String,
         myBookingsRequest: MyBookingsRequest,
         apiType: String
     ) {

         _loadingState.postValue(LoadingState.LOADING)

         viewModelScope.launch(Dispatchers.IO) {
             _dataMyBookings.postValue(
                 myBookingsRepository.myBookings(
                     authorization,
                     apiKey,
                     myBookingsRequest
                 ).body()
             )
         }
     } */

    var privilegesLiveData = MutableLiveData<PrivilegeResponseModel?>()

    fun updatePrivileges(privileges: PrivilegeResponseModel?) {
        privilegesLiveData.value = privileges
    }

    fun myBookingsApi(
        apiKey: String,
        responseFormat: String,
        fromDate : String,
        toDate: String,
        locale: String,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            myBookingsRepository.myNewBookings(
                apiKey,responseFormat,fromDate,toDate,1,locale
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _dataMyBookings.postValue(
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
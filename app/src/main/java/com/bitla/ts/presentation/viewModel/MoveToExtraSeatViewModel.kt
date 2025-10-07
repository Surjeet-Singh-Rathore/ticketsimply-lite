package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitla.ts.domain.pojo.move_to_extra_seat.MoveToExtraSeat
import com.bitla.ts.domain.pojo.move_to_extra_seat.request.ReqBody
import com.bitla.ts.domain.pojo.move_to_normal_seats.MoveToNormalSeatRequest
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.repository.MoveToExtraSeatRepository
import com.bitla.ts.koin.networkModule.NetworkProcess
import com.bitla.ts.utils.LoadingState
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class MoveToExtraSeatViewModel<T : Any?>(private val moveToExtraSeatRepository: MoveToExtraSeatRepository) :
    ViewModel() {

    companion object {
        val TAG: String = MoveToExtraSeatViewModel::class.java.simpleName
    }

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState
    private val _moveToExtraSeat = MutableLiveData<MoveToExtraSeat>()
    private val _moveToNormalSeat = MutableLiveData<MoveToExtraSeat>()
    val moveToExtraSeat: LiveData<MoveToExtraSeat>
        get() = _moveToExtraSeat
    val moveToNormalSeat: LiveData<MoveToExtraSeat>
        get() = _moveToNormalSeat

    private val _moveQuotaSeat = MutableLiveData<MoveToExtraSeat>()
    val moveQuotaSeat: LiveData<MoveToExtraSeat>
        get() = _moveQuotaSeat



    private val _validation = MutableLiveData<Boolean>()
    val validationData: LiveData<Boolean>
        get() = _validation


    private var apiType: String? = null

    val messageSharedFlow = MutableSharedFlow<String>()

    var privilegesLiveData = MutableLiveData<PrivilegeResponseModel?>()

    fun updatePrivileges(privileges: PrivilegeResponseModel?) {
        privilegesLiveData.value = privileges
    }


    /* fun moveToExtraSeatApi(
         authorization: String,
         apiKey: String,
         moveToExtraSeatRequest: MoveToExtraSeatRequest,
         apiType: String
     ) {

         _loadingState.postValue(LoadingState.LOADING)
         val gson = GsonBuilder().disableHtmlEscaping().create()
         val json = gson.toJson(moveToExtraSeatRequest)

         viewModelScope.launch(Dispatchers.IO) {
             _moveToExtraSeat.postValue(
                 moveToExtraSeatRepository.moveToExtraSeat(
                     authorization,
                     apiKey,
                     moveToExtraSeatRequest
                 ).body()
             )
         }
     }*/

    fun moveToExtraSeatApi(
        moveToExtraSeatRequest: ReqBody,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)
        val gson = GsonBuilder().disableHtmlEscaping().create()
        val json = gson.toJson(moveToExtraSeatRequest)

        viewModelScope.launch(Dispatchers.IO) {
            moveToExtraSeatRepository.newMoveToExtraSeatApi(
                moveToExtraSeatRequest
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _moveToExtraSeat.postValue(
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



    fun moveQuotaSeatApi(
        moveToExtraSeatRequest: ReqBody,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)
        val gson = GsonBuilder().disableHtmlEscaping().create()
        val json = gson.toJson(moveToExtraSeatRequest)

        viewModelScope.launch(Dispatchers.IO) {
            moveToExtraSeatRepository.newMoveToExtraSeatApi(
                moveToExtraSeatRequest
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _moveToExtraSeat.postValue(
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




    fun moveToNormalSeatApi(
        moveToExtraSeatRequest: MoveToNormalSeatRequest,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)
        val gson = GsonBuilder().disableHtmlEscaping().create()
        val json = gson.toJson(moveToExtraSeatRequest)

        viewModelScope.launch(Dispatchers.IO) {
            moveToExtraSeatRepository.newMoveToNormalSeatApi(
                moveToExtraSeatRequest
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _moveToNormalSeat.postValue(
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
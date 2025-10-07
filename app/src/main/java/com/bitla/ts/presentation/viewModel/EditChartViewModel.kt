package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitla.ts.domain.pojo.edit_chart.EditChart
import com.bitla.ts.domain.repository.EditChartRepository
import com.bitla.ts.koin.networkModule.NetworkProcess
import com.bitla.ts.utils.LoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch


class EditChartViewModel<T : Any?>(private val editChartRepository: EditChartRepository) :
    ViewModel() {

    companion object {
        val TAG: String = EditChartViewModel::class.java.simpleName
    }

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    private val _editChart = MutableLiveData<EditChart>()
    val editChart: LiveData<EditChart>
        get() = _editChart

    private val _validation = MutableLiveData<String>()
    val validationData: LiveData<String>
        get() = _validation

    private val _etOnChange = MutableLiveData<Boolean>()
    val etOnChange: LiveData<Boolean>
        get() = _etOnChange


    private var apiType: String? = null
    val messageSharedFlow = MutableSharedFlow<String>()



    fun validation(
        seatNo: String?,
        name: String?,
        phoneNo: String?,
        phoneNoCount: Int,
        boardingPoint: String,
        droppingPoint: String,
        validateSeatNo: String,
        validateName: String,
        validatePhoneNo: String,
        validateBoardingPoint: String,
        validateDroppingPoint: String
    ) {
        when {
            seatNo == null || seatNo.isEmpty() -> _validation.postValue(validateSeatNo)
            name == null || name.isEmpty() -> _validation.postValue(validateName)
            phoneNo == null || phoneNo.isEmpty() || phoneNo.count() < phoneNoCount -> _validation.postValue(
                validatePhoneNo
            )
            boardingPoint.isEmpty() -> _validation.postValue(validateBoardingPoint)
            droppingPoint.isEmpty() -> _validation.postValue(validateDroppingPoint)
            else -> _validation.postValue("")
        }
    }


    fun etTextWatcher(
        seatNo: String?,
        name: String?,
        phoneNo: String?,
        phoneNoCount: Int,
        boardingPoint: String,
        droppingPoint: String
    ) {
        if (seatNo != null && name != null && phoneNo != null && seatNo.isNotEmpty() && name.isNotEmpty() && phoneNo.isNotEmpty() && boardingPoint.isNotEmpty() && droppingPoint.isNotEmpty())
            _etOnChange.postValue(true)
        else
            _etOnChange.postValue(false)
    }



    /*fun editChartApi(
        authorization: String,
        apiKey: String,
        editChartRequest: EditChartRequest,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            _editChart.postValue(
                editChartRepository.editChart(
                    authorization,
                    apiKey,
                    editChartRequest = editChartRequest
                ).body()
            )
        }
    }*/

    fun editChartApi(
        editChartRequest: com.bitla.ts.domain.pojo.edit_chart.request.ReqBody,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            editChartRepository.newEditChart(
                editChartRequest = editChartRequest
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _editChart.postValue(
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
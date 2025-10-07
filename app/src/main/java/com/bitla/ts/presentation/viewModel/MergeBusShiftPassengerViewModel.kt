package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bitla.ts.domain.repository.MergeBusShiftRepository
import com.bitla.ts.presentation.view.merge_bus.pojo.ShiftToServicesListResponse
import com.bitla.ts.utils.LoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


open class MergeBusShiftPassengerViewModel<T : Any?>(private val mergeBusRepository: MergeBusShiftRepository) :
    BaseViewModel(),
    Callback<T> {

    companion object {
        val TAG: String = MergeBusShiftPassengerViewModel::class.java.simpleName
    }

     var origin=MutableLiveData<String>()
     var destination=MutableLiveData<String>()
     var busType=MutableLiveData<String>()
     var resId=MutableLiveData<String>()
     var toolBarHeader=MutableLiveData<String>()
     var travelDate=MutableLiveData<String>()
    var originIdLeftCoach=MutableLiveData<String>()
    var destinationIdLeftCoach=MutableLiveData<String>()
    var destinationIdRightCoach=MutableLiveData<String>()

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState


    private val _shiftToServicesList = MutableLiveData<ShiftToServicesListResponse>()
    val shiftToServicesList: LiveData<ShiftToServicesListResponse>
        get() = _shiftToServicesList


    fun getShiftToServicesList(
        apiKey: String,
        originId: String,
        destinationId: String,
        responseFormat: String,
        travelDate: String,
        locale: String,
        oldResId: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            _shiftToServicesList.postValue(
                mergeBusRepository.getShiftToServices(
                    originId,
                    destinationId,
                    travelDate,
                    apiKey,
                    responseFormat,
                    locale,
                    oldResId
                ).body()
            )
        }
    }

    override fun onResponse(call: Call<T>, response: Response<T>) {

    }

    override fun onFailure(call: Call<T>, t: Throwable) {

    }

}
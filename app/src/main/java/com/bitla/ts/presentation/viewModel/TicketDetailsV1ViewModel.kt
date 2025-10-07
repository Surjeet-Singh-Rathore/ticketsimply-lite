package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitla.ts.data.ticket_details_method_name
import com.bitla.ts.domain.pojo.ticket_details_phase_3.response.TicketDetailsResponse
import com.bitla.ts.domain.repository.TicketDetailsRepository
import com.bitla.ts.koin.networkModule.NetworkProcess
import com.bitla.ts.utils.LoadingState
import com.bitla.ts.utils.common.getRetrofitErrorMsg
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class TicketDetailsV1ViewModel<T : Any?>(private val ticketDetailsRepository: TicketDetailsRepository): BaseViewModel(),
    Callback<T> {

    private var apiType: String? = null
    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    private val _dataTicketDetails = MutableLiveData<TicketDetailsResponse>()
    val dataTicketDetails: LiveData<TicketDetailsResponse>
        get() = _dataTicketDetails







    override fun onFailure(call: Call<T>, t: Throwable) {
        _loadingState.postValue(LoadingState.error(t.message))
    }

    override fun onResponse(
        call: Call<T>,
        response: Response<T>
    ) {
        try {
            if (response.isSuccessful) {
                Timber.d("responseBody ${response.body()}")
                if (apiType == ticket_details_method_name)
                    _dataTicketDetails.postValue(response.body() as TicketDetailsResponse)
                _loadingState.postValue(LoadingState.LOADED)
            } else {
                val message = getRetrofitErrorMsg(response.errorBody())
                _loadingState.postValue(LoadingState.error(message))
            }
        } catch (e: Exception) {
            _loadingState.postValue(LoadingState.error(e.message))
            Timber.d("ExceptionMsg ${e.message}")
        }
    }
}
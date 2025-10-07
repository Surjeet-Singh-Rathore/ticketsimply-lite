package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitla.ts.domain.pojo.phonepe.PhonePeStatusResponse
import com.bitla.ts.domain.pojo.send_sms_email.SendSmsEmailResponse
import com.bitla.ts.domain.pojo.send_sms_email.request.ReqBody
import com.bitla.ts.domain.pojo.ticket_details.response.TicketDetailsModel
import com.bitla.ts.domain.repository.TicketDetailsRepository
import com.bitla.ts.koin.networkModule.NetworkProcess
import com.bitla.ts.utils.LoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch


class TicketDetailsViewModel<T : Any?>(private val ticketDetailsRepository: TicketDetailsRepository) :
    ViewModel() {

    companion object {
        val TAG: String = TicketDetailsViewModel::class.java.simpleName
    }

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    private val _dataTicketDetails = MutableLiveData<TicketDetailsModel>()
    val dataTicketDetails: LiveData<TicketDetailsModel>
        get() = _dataTicketDetails



    private val _dataSMSEmailResponse = MutableLiveData<SendSmsEmailResponse>()
    val dataSMSEmailResponse: LiveData<SendSmsEmailResponse>
        get() = _dataSMSEmailResponse


    private var apiType: String? = null

    val messageSharedFlow = MutableSharedFlow<String>()


    /*fun ticketDetailsApi(
        authorization: String,
        apiKey: String,
        ticketDetailsRequest: TicketDetailsRequest,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            _dataTicketDetails.postValue(
                ticketDetailsRepository.ticketDetails(
                    authorization,
                    apiKey,
                    ticketDetailsRequest
                ).body()
            )
        }
    } */

    fun ticketDetailsApi(
        apiKey: String,
        ticketNumber: String,
        jsonFormat: Boolean,
        isQrScan: Boolean,
        locale: String,
        apiType: String


    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            ticketDetailsRepository.newTicketDetails(
                apiKey,
                ticketNumber, jsonFormat, isQrScan, locale
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _dataTicketDetails.postValue(
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




    /*fun sendSMSEmailApi(
        authorization: String,
        apiKey: String,
        sendSMSEmailRequest: SendSMSEmailRequest,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            _dataSMSEmailResponse.postValue(
                ticketDetailsRepository.sendSmsEmail(
                    authorization,
                    apiKey,
                    sendSMSEmailRequest
                ).body()
            )
        }
    }*/

    fun sendSMSEmailApi(
        sendSMSEmailRequest: ReqBody,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            ticketDetailsRepository.newSendSmsEmail(
                sendSMSEmailRequest
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _dataSMSEmailResponse.postValue(
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
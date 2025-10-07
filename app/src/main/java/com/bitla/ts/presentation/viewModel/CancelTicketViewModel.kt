package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitla.ts.domain.pojo.bulk_ticket_update.request.ReqBody
import com.bitla.ts.domain.pojo.bulk_ticket_update.response.BulkTicketUpdateResponseModel
import com.bitla.ts.domain.pojo.cancel_partial_ticket_model.response.CancelPartialTicketResponse
import com.bitla.ts.domain.pojo.cancellation_details_model.request.ReqBody2
import com.bitla.ts.domain.pojo.cancellation_details_model.response.CancellationDetailsResponse
import com.bitla.ts.domain.pojo.confirm_otp_cancel_partial_ticket_model.response.ConfirmOtpCancelPartialTicketResponse
import com.bitla.ts.domain.pojo.confirm_otp_release_phone_block_tickets_model.response.ConfirmOtpReleasePhoneBlockTicketResponse
import com.bitla.ts.domain.pojo.update_ticket.response.UpdateTicketResponseModel
import com.bitla.ts.domain.repository.CancelTicketRepository
import com.bitla.ts.koin.networkModule.NetworkProcess
import com.bitla.ts.utils.LoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class CancelTicketViewModel<T : Any?>(private val cancelTicketRepository: CancelTicketRepository) :
    ViewModel() {

    companion object {
        val TAG: String = CancelTicketViewModel::class.java.simpleName
    }


    private val _cancelPartialTicketResponse = MutableLiveData<CancelPartialTicketResponse>()
    val cancelPartialTicketViewModel: LiveData<CancelPartialTicketResponse>
        get() = _cancelPartialTicketResponse

    private val _confirmOtpCcancelPartialTicketResponse =
        MutableLiveData<ConfirmOtpCancelPartialTicketResponse>()
    val confirmOtpCancelPartialTicketResponse: LiveData<ConfirmOtpCancelPartialTicketResponse>
        get() = _confirmOtpCcancelPartialTicketResponse


    private val _confirmOtpReleasePhoneBlockTicketResponse =
        MutableLiveData<ConfirmOtpReleasePhoneBlockTicketResponse>()
    val confirmOtpReleasePhoneBlockTicketResponse: LiveData<ConfirmOtpReleasePhoneBlockTicketResponse>
        get() = _confirmOtpReleasePhoneBlockTicketResponse


    private val _cancellationDetailsResponse = MutableLiveData<CancellationDetailsResponse>()
    val cancellationDetailsResponse: LiveData<CancellationDetailsResponse>
        get() = _cancellationDetailsResponse


    private val _bulkTicketUpdateResponse = MutableLiveData<BulkTicketUpdateResponseModel>()
    val bulkTicketUpdateResponse: LiveData<BulkTicketUpdateResponseModel>
        get() = _bulkTicketUpdateResponse

    private val _updateTicketResponseModel = MutableLiveData<UpdateTicketResponseModel>()
    val updateTicketResponseModel: LiveData<UpdateTicketResponseModel>
        get() = _updateTicketResponseModel


    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState> get() = _loadingState

    private var apiType: String? = null

    val messageSharedFlow = MutableSharedFlow<String>()



    /*   fun getConfirmOtpReleasePhoneBlockTicketApi(
           authorization: String,
           apiKey: String,
           confirmOtpReleasePhoneBlockTicketRequest: ConfirmOtpReleasePhoneBlockTicketRequest,
           apiType: String
       ) {

           _loadingState.postValue(LoadingState.LOADING)

           viewModelScope.launch(Dispatchers.IO) {
               _confirmOtpReleasePhoneBlockTicketResponse.postValue(
                   cancelTicketRepository.getConfirmOtpReleaseTicketRequest(
                       authorization,
                       apiKey,
                       confirmOtpReleasePhoneBlockTicketRequest
                   ).body()
               )
           }
       }
   */
    fun getConfirmOtpReleasePhoneBlockTicketApi(
        confirmOtpReleasePhoneBlockTicketRequest: com.bitla.ts.domain.pojo.confirm_otp_release_phone_block_tickets_model.request.ReqBody,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            cancelTicketRepository.newGetConfirmOtpReleaseTicketRequest(
                confirmOtpReleasePhoneBlockTicketRequest
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _confirmOtpReleasePhoneBlockTicketResponse.postValue(
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


    /*fun getCancelPartialTicketApi(
        authorization: String,
        apiKey: String,
        cancelPartialTicketRequest: CancelPartialTicketRequest,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            _cancelPartialTicketResponse.postValue(
                cancelTicketRepository.getCancelPartialTicketRequest(
                    authorization,
                    apiKey,
                    cancelPartialTicketRequest
                ).body()
            )
        }
    } */

    fun getCancelPartialTicketApi(
        cancelPartialTicketRequest: com.bitla.ts.domain.pojo.cancel_partial_ticket_model.request.ReqBody,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            cancelTicketRepository.newGetCancelPartialTicketRequest(
                cancelPartialTicketRequest
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _cancelPartialTicketResponse.postValue(
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

    fun getConfirmOtpCancelPartialTicketApi(
        confirmOtpCancelPartialTicketRequest: com.bitla.ts.domain.pojo.confirm_otp_cancel_partial_ticket_model.request.ReqBody,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            cancelTicketRepository.newGetConfirmOtpCancelPartialTicketRequest(
                confirmOtpCancelPartialTicketRequest
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _confirmOtpCcancelPartialTicketResponse.postValue(
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
    /*fun getCancellationDetailsApi(
        authorization: String,
        apiKey: String,
        cancellationDetailsRequest: CancellationDetailsRequest,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            _cancellationDetailsResponse.postValue(
                cancelTicketRepository.getCancelTicketRequest(
                    authorization,
                    apiKey,
                    cancellationDetailsRequest
                ).body()
            )
        }
    }*/

    fun getCancellationDetailsApi(
        cancellationDetailsRequest: com.bitla.ts.domain.pojo.cancellation_details_model.request.ReqBody,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            cancelTicketRepository.newGetCancelTicketRequest(
                cancellationDetailsRequest
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _cancellationDetailsResponse.postValue(
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

    fun getZeroCancellationDetailsApi(
        cancellationDetailsRequest: ReqBody2,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            cancelTicketRepository.newGetZeroCancellationDetailsTicket(
                cancellationDetailsRequest
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _cancellationDetailsResponse.postValue(
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

    /* fun getBulkTicketUpdateApi(
         authorization: String,
         apiKey: String,
         bulkTicketUpdateRequestModel: BulkTicketUpdateRequestModel,
         apiType: String
     ) {

         _loadingState.postValue(LoadingState.LOADING)

         viewModelScope.launch(Dispatchers.IO) {
             _bulkTicketUpdateResponse.postValue(
                 cancelTicketRepository.getBulkTicketUpdateApiRequest(
                     authorization,
                     apiKey,
                     bulkTicketUpdateRequestModel
                 ).body()
             )
         }
     }
     */


    fun getBulkTicketUpdateApi(
        bulkTicketUpdateRequestModel: ReqBody,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            cancelTicketRepository.newGetBulkTicketUpdateApiRequest(
                bulkTicketUpdateRequestModel
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _bulkTicketUpdateResponse.postValue(
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
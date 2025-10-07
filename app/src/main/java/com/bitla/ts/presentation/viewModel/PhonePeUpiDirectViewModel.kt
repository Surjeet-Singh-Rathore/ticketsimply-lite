package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bitla.ts.domain.pojo.phonepe_direct_upi_for_app.request.PhonePeDirectUPIForAppRequest
import com.bitla.ts.domain.pojo.phonepe_direct_upi_for_app.response.PhonePeDirectUPIForAppResponse
import com.bitla.ts.domain.pojo.phonepe_direct_upi_transaction_status.request.PhonePeDirectUPITransactionStatusRequest
import com.bitla.ts.domain.pojo.phonepe_direct_upi_transaction_status.response.PhonePeDirectUPITransactionStatusResponse
import com.bitla.ts.domain.pojo.phonepe_direct_validate_upi_id.response.PhonePeDirectValidateUpiIdResponse
import com.bitla.ts.domain.repository.PhonePeUpiDirectRepository
import com.bitla.ts.koin.networkModule.NetworkProcess
import com.bitla.ts.utils.LoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class PhonePeUpiDirectViewModel<T : Any?>(private val phonePeUpiDirectRepository: PhonePeUpiDirectRepository) :
    BaseViewModel() {

    companion object {
        val tag: String = PhonePeUpiDirectViewModel::class.java.simpleName
    }

    private val _phonePeDirectValidateUpiId = MutableLiveData<PhonePeDirectValidateUpiIdResponse>()
    val phonePeDirectValidateUpiId: LiveData<PhonePeDirectValidateUpiIdResponse>
        get() = _phonePeDirectValidateUpiId

    private val _phonePeDirectUPITransactionStatus = MutableLiveData<PhonePeDirectUPITransactionStatusResponse>()
    val phonePeDirectUPITransactionStatus: LiveData<PhonePeDirectUPITransactionStatusResponse>
        get() = _phonePeDirectUPITransactionStatus

    private val _phonePeDirectUPIForApp = MutableLiveData<PhonePeDirectUPIForAppResponse>()
    val phonePeDirectUPIForApp: LiveData<PhonePeDirectUPIForAppResponse>
        get() = _phonePeDirectUPIForApp

    private var apiType: String? = null

    val messageSharedFlow = MutableSharedFlow<String>()


    fun callPhonePeDirectValidateUpiId(
        apiKey: String, vpa: String, apiType: String
    ) {


        viewModelScope.launch(Dispatchers.IO) {
            phonePeUpiDirectRepository.getPhonePeDirectValidateUpiId(
                apiKey, vpa
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _phonePeDirectValidateUpiId.postValue(
                            it.data
                        )
                    }

                    is NetworkProcess.Failure -> {
                        messageSharedFlow.emit(it.message)
                    }
                }
            }
        }
    }

    fun callPhonePeDirectUPITransactionStatus(
        phonePeDirectUPITransactionStatusRequest: PhonePeDirectUPITransactionStatusRequest,
        apiType: String
    ) {


        viewModelScope.launch(Dispatchers.IO) {
            delay(5000)
            phonePeUpiDirectRepository.getPhonePeDirectTransactionStatus(
                phonePeDirectUPITransactionStatusRequest
            ) .collect {
                    when (it) {
                        is NetworkProcess.Loading -> {}
                        is NetworkProcess.Success -> {
                            _phonePeDirectUPITransactionStatus   .postValue(
                                    it.data
                                )
                        }

                        is NetworkProcess.Failure -> {
                            messageSharedFlow.emit(it.message)
                        }
                    }
                }
        }
    }

    fun callPhonePeDirectUPIForApp(
        apiKey: String,
        phonePeDirectUPIForAppRequest: PhonePeDirectUPIForAppRequest,
        apiType: String
    ) {


        viewModelScope.launch(Dispatchers.IO) {

            phonePeUpiDirectRepository.getPhonePeDirectUpiForApp(
                apiKey,
                phonePeDirectUPIForAppRequest
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _phonePeDirectUPIForApp .postValue(
                            it.data
                        )
                }

                is NetworkProcess.Failure -> {
                    messageSharedFlow.emit(it.message)
                }
            }
        }
        }
    }
}
package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitla.ts.domain.pojo.PhonePeV2StatusResponse
import com.bitla.ts.domain.pojo.agent_recharge.AgentRechargeResponseModel
import com.bitla.ts.domain.pojo.agent_recharge.BranchRechargeResponseModel
import com.bitla.ts.domain.pojo.agent_recharge.request.AgentReqBody
import com.bitla.ts.domain.pojo.agent_recharge.request.ConfirmAgentRequestBody
import com.bitla.ts.domain.pojo.agent_recharge.request.ReqBody
import com.bitla.ts.domain.pojo.instant_recharge.AgentPGDataResponse
import com.bitla.ts.domain.pojo.instant_recharge.GetAgentRechargeResponse
import com.bitla.ts.domain.pojo.phonepe.PhonePeResponse
import com.bitla.ts.domain.pojo.phonepe.PhonePeStatusResponse
import com.bitla.ts.domain.repository.BranchRechargeRepository
import com.bitla.ts.koin.networkModule.NetworkProcess
import com.bitla.ts.utils.LoadingState
import com.bitla.ts.utils.Location
import com.bitla.ts.utils.common.getDateYMD
import com.google.android.gms.common.api.internal.ApiKey
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import retrofit2.http.Query
import timber.log.Timber

class AgentRechargeViewModel<T : Any?>(private val branchRechargeRepository: BranchRechargeRepository) :
    ViewModel() {

    companion object {
        val TAG: String = AgentRechargeViewModel::class.java.simpleName
    }

    private val _loadingState = MutableLiveData<LoadingState>()

    //private val
    val loadingState: LiveData<LoadingState>
        get() = _loadingState


    private val _branchRechargeDetails = MutableLiveData<BranchRechargeResponseModel>()
    val branchRecharge: LiveData<BranchRechargeResponseModel>
        get() = _branchRechargeDetails

    private val _confirmBranchRechargeDetails = MutableLiveData<BranchRechargeResponseModel>()
    val confirmBranchRecharge: LiveData<BranchRechargeResponseModel>
        get() = _confirmBranchRechargeDetails

    private val _agentRechargeDetails = MutableLiveData<AgentRechargeResponseModel>()
    val agentRecharge: LiveData<AgentRechargeResponseModel>
        get() = _agentRechargeDetails


    private val _confirmAgentRechargeDetails = MutableLiveData<AgentRechargeResponseModel>()
    val confirmAgentRecharge: LiveData<AgentRechargeResponseModel>
        get() = _confirmAgentRechargeDetails

    private val _validation = MutableLiveData<Boolean>()
    val validationData: LiveData<Boolean>
        get() = _validation

    private val _getAgentRechargeData = MutableLiveData<GetAgentRechargeResponse>()
    val getAgentRechargeData: LiveData<GetAgentRechargeResponse>
        get() = _getAgentRechargeData

    private val _getAgentPGData = MutableLiveData<AgentPGDataResponse>()
    val getAgentPGData: LiveData<AgentPGDataResponse>
        get() = _getAgentPGData

    private val _getPhonePePageTransactionStatus = MutableLiveData<PhonePeResponse>()
    val getPhonePePageTransactionStatus: LiveData<PhonePeResponse>
        get() = _getPhonePePageTransactionStatus

    private val _getPhonePeTransStatus = MutableLiveData<PhonePeStatusResponse>()
    val getPhonePeTransStatus: LiveData<PhonePeStatusResponse>
        get() = _getPhonePeTransStatus

    private val _getRazorPaySuccess = MutableLiveData<String>()

    val getRazorPaySuccess: LiveData<String>
        get() = _getRazorPaySuccess

    private val _getEaseBuzzSuccess = MutableLiveData<AgentPGDataResponse>()

    val getEaseBuzzSuccess: LiveData<AgentPGDataResponse>
        get() = _getEaseBuzzSuccess

    private val _getPayBitlaSuccess = MutableLiveData<AgentPGDataResponse>()

    val getPayBitlaSuccess : LiveData<AgentPGDataResponse>
        get() = _getPayBitlaSuccess

    private val _getRazorPayFailure = MutableLiveData<String>()

    val getRazorPayFailure: LiveData<String>
        get() = _getRazorPayFailure

    private val _phonePeV2StatusResponse = MutableLiveData<PhonePeV2StatusResponse>()
    val phonePeV2StatusResponse: LiveData<PhonePeV2StatusResponse>
        get() = _phonePeV2StatusResponse


    private var apiType: String? = null

    val messageSharedFlow = MutableSharedFlow<String>()


    fun branchRechargeApi(
        branchRechargeRequest: ReqBody,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)
        val gson = GsonBuilder().disableHtmlEscaping().create()
        val json = gson.toJson(branchRechargeRequest)
        viewModelScope.launch(Dispatchers.IO) {
            branchRechargeRepository.newBranchRecharge(
                branchRechargeRequest
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _branchRechargeDetails.postValue(
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

    fun razorpayRechargeApiSuccess(
        pnrNumber: String,
        paymentId: String
    ) {


        viewModelScope.async(Dispatchers.IO) {

            branchRechargeRepository.getRazorPaySuccess(
                pnrNumber = pnrNumber,
                paymentId = paymentId
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {

                    }

                    is NetworkProcess.Failure -> {
                        messageSharedFlow.emit(it.message)
                    }
                }
            }

        }
    }

    fun easeBuzzRechargeApiSuccess(
        isEaseBuzzPayment: Boolean,
        pnrNumber: String,
        amount: String,
        phoneNo: String,
        emailId: String,

        ) {


        viewModelScope.async(Dispatchers.IO) {
            branchRechargeRepository.getEasebuzzSuccess(
                isEaseBuzzPayment = isEaseBuzzPayment,
                pnrNumber = pnrNumber,
                amount = amount,
                phoneNo = phoneNo,
                emailId = emailId

            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _getEaseBuzzSuccess.postValue(
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
    fun payBitlaRechargeApiSuccess(
        pnrNumber: String,
    ){
        viewModelScope.async(Dispatchers.IO) {
            _getPayBitlaSuccess.postValue(
                branchRechargeRepository.getPayBitlaSuccess(
                    pnrNumber = pnrNumber,

                ).body()
            )
        }
    }

    fun razorpayRechargeApiFailure(
        pnrNumber: String,
        orderId: String
    ) {


        viewModelScope.async(Dispatchers.IO) {
            //_getRazorPayFailure.postValue(
            branchRechargeRepository.getRazorPayFailure(
                pnrNumber = pnrNumber,
                orderId = orderId
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {

                    }

                    is NetworkProcess.Failure -> {
                        messageSharedFlow.emit(it.message)
                    }
                }
            }

            //)
        }
    }
    /*fun confirmBranchRechargeApi(
        authorization: String,
        apiKey: String,
        branchRechargeRequest: ConfirmAgentRechargeRequest,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)
        val gson = GsonBuilder().disableHtmlEscaping().create()
        val json = gson.toJson(branchRechargeRequest)

        viewModelScope.launch(Dispatchers.IO) {
            _confirmBranchRechargeDetails.postValue(
                branchRechargeRepository.confirmBranchRecharge(
                    authorization,
                    apiKey,
                    branchRechargeRequest
                ).body()
            )
        }
    }  */

    fun confirmBranchRechargeApi(
        branchRechargeRequest: ConfirmAgentRequestBody,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)
        val gson = GsonBuilder().disableHtmlEscaping().create()
        val json = gson.toJson(branchRechargeRequest)
        viewModelScope.launch(Dispatchers.IO) {
            branchRechargeRepository.newConfirmBranchRecharge(
                branchRechargeRequest
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _confirmBranchRechargeDetails.postValue(
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

    fun getAgentTransactionDetailApi(
        apiKey: String,
        amount: String,
        locale: String

    ) {

        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            branchRechargeRepository.getAgentTransactionDetailApi(
                apiKey = apiKey, amount, locale
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _getAgentRechargeData .postValue(
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

    fun getAgentPGDetailApi(
        apiKey: String,
        amount: String,
        pgType: String,
        nativeAppType: Int

    ) {

        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            branchRechargeRepository.getAgentPGDetailApi(
                apiKey = apiKey, amount, pgType, nativeAppType
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _getAgentPGData .postValue(
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

    fun getPhonePeStatusApi(
        apiKey: String,
        pnrNumber: String

    ) {

        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            branchRechargeRepository.getPhonePeStatusApi(
                apiKey = apiKey, pnrNumber
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _getPhonePeTransStatus.postValue(
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

    fun getPhonepePayPageTransactionStatusApi(
        xVerify: String,
        body: RequestBody,

        ) {

        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            branchRechargeRepository.getPhonepePayPageTransactionStatus(
                xVerify, body = body
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _getPhonePePageTransactionStatus.postValue(
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

    fun agentRechargeApi(
        agentRechargeRequest: AgentReqBody,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)
        val gson = GsonBuilder().disableHtmlEscaping().create()
        val json = gson.toJson(agentRechargeRequest)

        viewModelScope.launch(Dispatchers.IO) {
            branchRechargeRepository.newAgentRecharge(
                agentRechargeRequest
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _agentRechargeDetails.postValue(
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


    /* fun confirmAgentRechargeApi(
         authorization: String,
         apiKey: String,
         confirmAgentRechargeRequest: ConfirmAgentRechargeRequest,
         apiType: String
         ) {

             _loadingState.postValue(LoadingState.LOADING)
             val gson = GsonBuilder().disableHtmlEscaping().create()
             val json = gson.toJson(confirmAgentRechargeRequest)

             viewModelScope.launch(Dispatchers.IO) {
                 _confirmAgentRechargeDetails.postValue(
                     branchRechargeRepository.confirmAgentRecharge(
                         authorization,
                         apiKey,
                         confirmAgentRechargeRequest
                     ).body()
                 )
             }
         }  */

    fun confirmAgentRechargeApi(
        confirmAgentRechargeRequest: ConfirmAgentRequestBody,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)
        val gson = GsonBuilder().disableHtmlEscaping().create()
        val json = gson.toJson(confirmAgentRechargeRequest)
        viewModelScope.launch(Dispatchers.IO) {
            branchRechargeRepository.newConfirmAgentRecharge(
                confirmAgentRechargeRequest
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _confirmAgentRechargeDetails.postValue(
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

    // fields validation
    fun validation(
        selectedAgent: String,
        transactionType: String,
        toDate: String,
        amount: String,
        paymentType: String,
        description: String,
        status: String
    ) {

        Timber.d("all data $selectedAgent $transactionType ${getDateYMD((toDate))} $amount $paymentType $description $status")
        if (selectedAgent.isNotEmpty() &&
            transactionType.isNotEmpty() &&
            toDate.isNotEmpty() &&
            amount.isNotEmpty() &&
            paymentType.isNotEmpty() &&
            description.isNotEmpty() &&
            status.isNotEmpty()
        ) {
            _validation.postValue(true)
        } else {
            _validation.postValue(false)
        }


    }

    fun getPhonePeV2Status(
        apiKey: String,
        orderId: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            branchRechargeRepository.getPhonePeV2Status(
                apiKey = apiKey,
                orderId = orderId
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}

                    is NetworkProcess.Success -> {
                        _phonePeV2StatusResponse.postValue(
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

    fun phonePeV2RechargeSuccessConPay(
        pnrNumber: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            branchRechargeRepository.phonePeV2RechargeSuccessConPay(
                pnrNumber
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {}

                    is NetworkProcess.Failure -> {
                        messageSharedFlow.emit(it.message)
                    }
                }
            }
        }
    }
}
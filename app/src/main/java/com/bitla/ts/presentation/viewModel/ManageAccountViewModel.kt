package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitla.ts.domain.pojo.manage_account_view.get_transaction_pdf_url.request.ReqBody
import com.bitla.ts.domain.pojo.manage_account_view.get_transaction_pdf_url.response.GetTransactionPdfUrlResponse
import com.bitla.ts.domain.pojo.manage_account_view.manage_transaction_search.response.ManageTransactionSearchResponse
import com.bitla.ts.domain.pojo.manage_account_view.show_transaction_list.response.ShowTransactionListResponse
import com.bitla.ts.domain.pojo.manage_account_view.transaction_info.response.TransactionInformationResponse
import com.bitla.ts.domain.pojo.manage_account_view.update_account_status.request.UpdateAccountStatusRequest
import com.bitla.ts.domain.pojo.manage_account_view.update_account_status.response.UpdateAccountStatusResponse
import com.bitla.ts.domain.repository.ManageAccountRepository
import com.bitla.ts.koin.networkModule.NetworkProcess
import com.bitla.ts.utils.LoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class ManageAccountViewModel<T : Any?>(private val manageAccountRepository: ManageAccountRepository) :
    ViewModel() {


    companion object {
        val TAG: String = ManageAccountViewModel::class.java.simpleName
    }


    private var apiType: String? = null


    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState> get() = _loadingState

    private val _showTransactionListResponse = MutableLiveData<ShowTransactionListResponse>()
    val showTransactionListResponse: LiveData<ShowTransactionListResponse>
        get() = _showTransactionListResponse

    private val _getTransactionPdfUrlResponse = MutableLiveData<GetTransactionPdfUrlResponse>()
    val getTransactionPdfUrlResponse: LiveData<GetTransactionPdfUrlResponse>
        get() = _getTransactionPdfUrlResponse

    private val _transactionInformationResponse = MutableLiveData<TransactionInformationResponse>()
    val transactionInformationResponse: LiveData<TransactionInformationResponse>
        get() = _transactionInformationResponse

    private val _updateAccountStatusResponse = MutableLiveData<UpdateAccountStatusResponse>()
    val updateAccountStatusResponse: LiveData<UpdateAccountStatusResponse>
        get() = _updateAccountStatusResponse

    private val _validation = MutableLiveData<Boolean>()
    val validationData: LiveData<Boolean>
        get() = _validation

    private val _manageTransactionSearchResponse = MutableLiveData<ManageTransactionSearchResponse>()
    val manageTransactionSearchResponse: LiveData<ManageTransactionSearchResponse>
        get() = _manageTransactionSearchResponse

    val messageSharedFlow = MutableSharedFlow<String>()


    fun validation(
        toDate: String,
    ) {

        if (toDate.isNotEmpty()
        ) {
            _validation.postValue(true)
        } else {
            _validation.postValue(false)
        }


    }

    fun showTransactionListApi(
        showTransactionListRequest: com.bitla.ts.domain.pojo.manage_account_view.show_transaction_list.request.ReqBody,
        methodName: String

    ) {

        this.apiType = methodName
        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                manageAccountRepository.getShowTransactionListApi(
                    showTransactionListRequest,
                ) .collect {
                    when (it) {
                        is NetworkProcess.Loading -> {}
                        is NetworkProcess.Success -> {
                            _loadingState.postValue(LoadingState.LOADED)
                            _showTransactionListResponse.postValue(
                                    it.data
                                )
                        }

                        is NetworkProcess.Failure -> {
                            _loadingState.postValue(LoadingState.LOADED)
                            messageSharedFlow.emit(it.message)
                        }
                    }
                }
            } catch (e: Exception) {
                Timber.d("exception- ${e.message}")
            }
        }
    }

    fun manageTransactionSearchApi(
        manageTransactionSearchReq: com.bitla.ts.domain.pojo.manage_account_view.manage_transaction_search.request.ReqBody,
        methodName: String
    ) {

        this.apiType = methodName
        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                manageAccountRepository.getManageTransactionSearchApi(
                    manageTransactionSearchReq,
                ).collect {
                    when (it) {
                        is NetworkProcess.Loading -> {}
                        is NetworkProcess.Success -> {
                            _loadingState.postValue(LoadingState.LOADED)
                            _manageTransactionSearchResponse .postValue(
                                    it.data
                                )
                        }

                        is NetworkProcess.Failure -> {
                            _loadingState.postValue(LoadingState.LOADED)
                            messageSharedFlow.emit(it.message)
                        }
                    }
                }

            } catch (e: Exception) {
                Timber.d("exception- ${e.message}")
            }
        }
    }

    fun getTransactionPdfUrlApi(
        getTransactionPdfUrlRequest: ReqBody,
        methodName: String
    ) {
        this.apiType = methodName
        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                manageAccountRepository.getTransactionPdfUrlApi(
                    getTransactionPdfUrlRequest,
                ) .collect {
                    when (it) {
                        is NetworkProcess.Loading -> {}
                        is NetworkProcess.Success -> {
                            _loadingState.postValue(LoadingState.LOADED)
                            _getTransactionPdfUrlResponse .postValue(
                                    it.data
                                )
                        }

                        is NetworkProcess.Failure -> {
                            _loadingState.postValue(LoadingState.LOADED)
                            messageSharedFlow.emit(it.message)
                        }
                    }
                }
            } catch (e: Exception) {
                Timber.d("exception- ${e.message}")
            }
        }
    }

    fun transactionInfoApiApi(
        transactionInfoRequest: com.bitla.ts.domain.pojo.manage_account_view.transaction_info.request.ReqBody,
        methodName: String
    ) {
        this.apiType = methodName
        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                manageAccountRepository.transactionInfoApi(
                    transactionInfoRequest,
                ).collect {
                    when (it) {
                        is NetworkProcess.Loading -> {}
                        is NetworkProcess.Success -> {
                            _loadingState.postValue(LoadingState.LOADED)
                            _transactionInformationResponse .postValue(
                                    it.data
                                )
                        }

                        is NetworkProcess.Failure -> {
                            _loadingState.postValue(LoadingState.LOADED)
                            messageSharedFlow.emit(it.message)
                        }
                    }
                }
            } catch (e: Exception) {
                Timber.d("exception- ${e.message}")
            }
        }
    }

    fun updateAccountStatusApi(
        reqBody: UpdateAccountStatusRequest,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            manageAccountRepository.updateAccountStatus(reqBody).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _updateAccountStatusResponse .postValue(
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
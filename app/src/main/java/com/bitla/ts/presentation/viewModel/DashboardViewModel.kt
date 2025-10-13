package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.*
import com.bitla.ts.domain.pojo.CreditInfoResponse
import com.bitla.ts.domain.pojo.book_ticket.release_ticket.request.*
import com.bitla.ts.domain.pojo.book_ticket.release_ticket.response.*
import com.bitla.ts.domain.pojo.dashboard_fetch.response.*
import com.bitla.ts.domain.pojo.dashboard_model.privilege.*
import com.bitla.ts.domain.pojo.dashboard_model.release_ticket.request.*
import com.bitla.ts.domain.pojo.dashboard_model.release_ticket.response.*
import com.bitla.ts.domain.pojo.dashboard_model.response.*
import com.bitla.ts.domain.pojo.dynamic_domain.*
import com.bitla.ts.domain.pojo.login_auth_post.request.LoginAuthPostRequest
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.logout_auth_post_req.FullLogoutReqBody
import com.bitla.ts.domain.pojo.logout_auth_post_req.LogoutReqBody
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.store_fcm.*
import com.bitla.ts.domain.repository.*
import com.bitla.ts.koin.networkModule.NetworkProcess
import com.bitla.ts.phase2.dashboard_pojo.occupancyCalendarModel.response.*
import com.bitla.ts.utils.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.security.EncrypDecryp
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import timber.log.*
import java.util.*

class DashboardViewModel<T : Any?>(private val dashboardRepository: DashboardRepository) :
    ViewModel() {


    companion object {
        val TAG: String = DashboardViewModel::class.java.simpleName
    }

    private val _occupancyCalendarViewModel = MutableLiveData<OccupancyCalendarResponse>()
    val occupancyCalendarViewModel: LiveData<OccupancyCalendarResponse>
        get() = _occupancyCalendarViewModel

    private val _dashboardSummaryViewModel = MutableLiveData<DashboardResponseModel>()
    val dashboardSummaryViewModelModel: LiveData<DashboardResponseModel>
        get() = _dashboardSummaryViewModel

    private val _releaseTicketResponseViewModel = MutableLiveData<ReleaseTicketResponse>()
    val releaseTicketResponseViewModel: LiveData<ReleaseTicketResponse>
        get() = _releaseTicketResponseViewModel
    
    private val _releaseAgentRechargBlockedSeatsResponseViewModel = MutableLiveData<ReleaseAgentRechargBlockedSeatsResponse>()
    val releaseAgentRechargBlockedSeatsResponseViewModel: LiveData<ReleaseAgentRechargBlockedSeatsResponse>
        get() = _releaseAgentRechargBlockedSeatsResponseViewModel

    private val _releaseBranchUserBlockedSeatsResponseViewModel = MutableLiveData<ReleaseAgentRechargBlockedSeatsResponse>()
    val releaseBranchUserBlockedSeatsResponseViewModel: LiveData<ReleaseAgentRechargBlockedSeatsResponse>
        get() = _releaseBranchUserBlockedSeatsResponseViewModel

    private val _privileges = MutableLiveData<PrivilegeModel>()
    val privileges: LiveData<PrivilegeModel> get() = _privileges

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState> get() = _loadingState

    private val _loginWithOTP = MutableLiveData<LoginModel>()
    val loginWithOTP: LiveData<LoginModel> get() = _loginWithOTP

    private val _loginUser = MutableLiveData<LoginModel>()
    val loginUser: LiveData<LoginModel> get() = _loginUser

    private val _validation = MutableLiveData<String>()
    val validationData: LiveData<String> get() = _validation

    private val _validationOtp = MutableLiveData<String>()
    val validationDataOtp: LiveData<String> get() = _validationOtp

    private val _etOnChange = MutableLiveData<Boolean>()
    val etOnChange: LiveData<Boolean> get() = _etOnChange

    private val _etOnChangeOTP = MutableLiveData<Boolean>()
    val etOnChangeOTP: LiveData<Boolean> get() = _etOnChangeOTP

    private val _resetUser = MutableLiveData<LoginModel>()
    val resetUser: LiveData<LoginModel> get() = _resetUser

    private val _logoutUser = MutableLiveData<LoginModel>()
    val logoutUser: LiveData<LoginModel> get() = _logoutUser

    private val _dataDynamicDomain = MutableLiveData<DynamicDomain>()
    val dataDynamicDomain: LiveData<DynamicDomain>
        get() = _dataDynamicDomain

    private val _storeFcmViewModel = MutableLiveData<StoreFcmKey>()
    val storeFcmViewModel: LiveData<StoreFcmKey>
        get() = _storeFcmViewModel

    private val _getDate = MutableLiveData<String>()
    val getDate: LiveData<String>
        get() = _getDate

    private var apiType: String? = null

    var isResetUserCall:Boolean?=false

    val messageSharedFlow = MutableSharedFlow<String>()

    private val _creditInfoData = MutableLiveData<CreditInfoResponse>()
    val creditInfoData: LiveData<CreditInfoResponse>
        get() = _creditInfoData


    /* fun confirmOTP(
         loginWithOtpRequest: LoginWithOtpRequest,
         apiType: String
     ) {

         _loadingState.postValue(LoadingState.LOADING)

         viewModelScope.launch(Dispatchers.IO) {
             _data.postValue(
                 dashboardRepository.getLoginWithOTPDetails(
                     loginWithOtpRequest
                 ).body()
             )
         }
     }*/

    var privilegesLiveData = MutableLiveData<PrivilegeResponseModel?>()

    fun updatePrivileges(privileges: PrivilegeResponseModel?) {
        privilegesLiveData.value = privileges
    }

    fun confirmOTP(
        loginWithOtpRequest: com.bitla.ts.domain.pojo.login_with_otp.request.ReqBody,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            dashboardRepository.newGetLoginWithOTPDetails(
                loginWithOtpRequest
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _loginWithOTP.postValue(
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


    fun initDynamicDomain(apiType: String) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            dashboardRepository.initDomain().collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _dataDynamicDomain.postValue(it.data)
                    }

                    is NetworkProcess.Failure -> {
                        messageSharedFlow.emit(it.message)
                    }
                }
            }
        }
    }

    fun fullLogoutApi(
        apiKey: String,
        deviceId: String,
        closeCounter: Boolean = false
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {

            if(EncrypDecryp.isEncrypted()){


                val reqBody=FullLogoutReqBody()
                reqBody.api_key=EncrypDecryp.getEncryptedValue(apiKey)
                reqBody.device_id=EncrypDecryp.getEncryptedValue(deviceId)
                reqBody.is_middle_tier=true
                reqBody.is_encrypted=true
                dashboardRepository.newFullPostLogout(reqBody,closeCounter).collect {
                    when (it) {
                        is NetworkProcess.Loading -> {}
                        is NetworkProcess.Success -> {
                            _logoutUser.postValue(
                                it.data
                            )
                        }

                        is NetworkProcess.Failure -> {
                            messageSharedFlow.emit(it.message)
                        }
                    }
                }
        }else{
                dashboardRepository.newFullLogout(apiKey, deviceId,closeCounter).collect {
                    when (it) {
                        is NetworkProcess.Loading -> {}
                        is NetworkProcess.Success -> {
                            _logoutUser.postValue(
                                it.data
                            )
                        }

                        is NetworkProcess.Failure -> {
                            messageSharedFlow.emit(it.message)
                        }
                    }
                }
        }}
    }

    fun loginApi(username: String, password: String, locale: String?, deviceId: String, shiftId: Int? = null, counterId: Int?= null, counterBalance: String = "") {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            if(EncrypDecryp.isEncrypted()==true) {
                val loginAuthPostRequest = LoginAuthPostRequest(
                    login = EncrypDecryp.getEncryptedValue(username),
                    password = EncrypDecryp.getEncryptedValue(password),
                    locale = locale,
                    device_id = EncrypDecryp.getEncryptedValue(deviceId),
                    is_encrypted = EncrypDecryp.isEncrypted(),
                    is_from_middle_tier = true,
                    shift_id = shiftId,
                    counter_id = counterId,
                    counter_balance = counterBalance
                )
                dashboardRepository.getNewLoginDetailsPost(loginAuthPostRequest)
                    .collect {
                        when (it) {
                            is NetworkProcess.Loading -> {}
                            is NetworkProcess.Success -> {
                                _loginUser.postValue(it.data)
                            }

                            is NetworkProcess.Failure -> {
                                messageSharedFlow.emit(it.message)
                            }
                        }
                    }
            } else {
                dashboardRepository.getNewLoginDetails(username, password, locale, deviceId, shiftId, counterId, counterBalance)
                    .collect {
                        when (it) {
                            is NetworkProcess.Loading -> {}
                            is NetworkProcess.Success -> {
                                _loginUser.postValue(it.data)
                            }

                            is NetworkProcess.Failure -> {
                                messageSharedFlow.emit(it.message)
                            }
                        }
                    }
            }


        }
    }

    /*fun resetApi(loginRequest: LoginRequest, apiType: String) {
      
        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            _dataLogout.postValue(
                dashboardRepository.getResetDetails(loginRequest).body()
            )
        }
    } */

    fun resetApi(username: String, pass: String, deviceId: String, shiftId: Int? = null, counterId: Int?= null, counterBalance: Double? = null) {


        val logOutReqBody= LogoutReqBody()
        logOutReqBody.login=EncrypDecryp.getEncryptedValue(username)
        logOutReqBody.password=EncrypDecryp.getEncryptedValue(pass)
        logOutReqBody.device_id=EncrypDecryp.getEncryptedValue(deviceId)
        logOutReqBody.is_encrypted=EncrypDecryp.isEncrypted()
        logOutReqBody.shift_id = shiftId
        logOutReqBody.counter_id = counterId
        logOutReqBody.counter_balance = counterBalance

        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {

            if(EncrypDecryp.isEncrypted()) {

                dashboardRepository.getNewResetPostApi(logOutReqBody).collect {
                    when (it) {
                        is NetworkProcess.Loading -> {}
                        is NetworkProcess.Success -> {
                            _resetUser.postValue(
                                it.data
                            )
                        }

                        is NetworkProcess.Failure -> {
                            messageSharedFlow.emit(it.message)
                        }
                    }
                }


        }else{
                dashboardRepository.getNewResetDetails(username, pass, deviceId, shiftId, counterId, counterBalance).collect {
                    when (it) {
                        is NetworkProcess.Loading -> {}
                        is NetworkProcess.Success -> {
                            _resetUser.postValue(
                                it.data
                            )
                        }
                        is NetworkProcess.Failure -> {
                            messageSharedFlow.emit(it.message)
                        }
                    }
                }
        }}

    }

    /* fun dashboardSummaryAPI(
         authorization: String,
         apiKey: String,
         dashboardRequestModel: DashboardRequestModel,
         apiType: String
     ) {

         _loadingState.postValue(LoadingState.LOADING)

         viewModelScope.launch(Dispatchers.IO) {
             _dashboardSummaryViewModel.postValue(
                 dashboardRepository.getDashboardData(
                     authorization,
                     apiKey,
                     dashboardRequestModel
                 ).body()
             )
         }
     } */

    fun dashboardSummaryAPI(
        reqBody: com.bitla.ts.domain.pojo.dashboard_model.request.ReqBody,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            dashboardRepository.newGetDashboardData(
                reqBody
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _dashboardSummaryViewModel.postValue(
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

    /*fun releaseTicketAPI(
        authorization: String,
        apiKey: String,
        releaseTicketRequest: ReleaseTicketRequest,
        apiType: String
    ) {

      
        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                _releaseTicketResponseViewModel.postValue(
                    dashboardRepository.getReleaseTicketData(
                        authorization,
                        apiKey,
                        releaseTicketRequest
                    ).body()
                )
            } catch (e: Exception) {
                Timber.d("releaseApiException- ${e.message}")
            }
        }
    }*/

    fun releaseBimaTicketAPI(
        releaseTicketRequest: ReqBody,
    ) {
        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                dashboardRepository.newGetReleaseBimaTicketData(
                    releaseTicketRequest
                ).collect {
                    when (it) {
                        is NetworkProcess.Loading -> {}
                        is NetworkProcess.Success -> {
                            _releaseTicketResponseViewModel.postValue(
                                it.data
                            )
                        }

                        is NetworkProcess.Failure -> {
                            messageSharedFlow.emit(it.message)
                        }
                    }
                }

            } catch (e: Exception) {
                Timber.d("releaseApiException- ${e.message}")
            }
        }
    }


    fun releaseTicketAPI(
        releaseTicketRequest: ReqBody,
        apiType: String
    ) {
        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                dashboardRepository.newGetReleaseTicketData(
                    releaseTicketRequest
                ).collect {
                    when (it) {
                        is NetworkProcess.Loading -> {}
                        is NetworkProcess.Success -> {
                            _loadingState.postValue(LoadingState.LOADED)
                            _releaseTicketResponseViewModel.postValue(
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
                Timber.d("releaseApiException- ${e.message}")
            }
        }
    }

    /* fun releaseTicketAPIWithoutTicket(
         authorization: String,
         apiKey: String,
         releaseTicketRequest: ReleaseTicketRequestWithoutTicket,
         apiType: String
     ) {


         _loadingState.postValue(LoadingState.LOADING)

         viewModelScope.launch(Dispatchers.IO) {
             try {
                 _releaseTicketResponseViewModel.postValue(
                     dashboardRepository.getReleaseTicketDataWithoutTicket(
                         authorization,
                         apiKey,
                         releaseTicketRequest
                     ).body()
                 )
             } catch (e: Exception) {
                 Timber.d("releaseApiException- ${e.message}")
             }
         }
     }*/

    fun releaseTicketAPIWithoutTicket(
        releaseTicketRequest: ReqBodyWithoutTicket,
        apiType: String
    ) {


        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                dashboardRepository.newGetReleaseTicketApiWithoutTicket(
                    releaseTicketRequest
                ).collect {
                    when (it) {
                        is NetworkProcess.Loading -> {}
                        is NetworkProcess.Success -> {
                            _releaseTicketResponseViewModel.postValue(
                                it.data
                            )
                        }

                        is NetworkProcess.Failure -> {
                            messageSharedFlow.emit(it.message)
                        }
                    }
                }

            } catch (e: Exception) {
                Timber.d("releaseApiException- ${e.message}")
            }
        }
    }
    
    fun releaseAgentRechargBlockedSeatsTicket(
        releaseTicketRequest: ReleaseAgentRechargBlockedSeatsRequest,
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                dashboardRepository.newGetReleaseAgentRechargBlockedSeatsResponse(
                    releaseTicketRequest
                ).collect {
                    when (it) {
                        is NetworkProcess.Loading -> {}
                        is NetworkProcess.Success -> {
                            _releaseAgentRechargBlockedSeatsResponseViewModel.postValue(
                                it.data
                            )
                        }

                        is NetworkProcess.Failure -> {
                            messageSharedFlow.emit(it.message)
                        }
                    }
                }

            } catch (e: Exception) {
                Timber.d("releaseApiException- ${e.message}")
            }
        }
    }

    fun releaseBranchUpiBlockedSeatsApi(
        releaseTicketRequest: ReleaseAgentRechargBlockedSeatsRequest,
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                dashboardRepository.releaseBranchUpiBlockedSeatsApi(
                    releaseTicketRequest
                ).collect {
                    when (it) {
                        is NetworkProcess.Loading -> {}
                        is NetworkProcess.Success -> {
                            _releaseBranchUserBlockedSeatsResponseViewModel.postValue(
                                it.data
                            )
                        }

                        is NetworkProcess.Failure -> {
                            messageSharedFlow.emit(it.message)
                        }
                    }
                }

            } catch (e: Exception) {
                Timber.d("releaseApiException- ${e.message}")
            }
        }
    }

    fun etTextWatcher(username: String, password: String) {
        if (username.isNotEmpty() && password.isNotEmpty())
            _etOnChange.postValue(true)
        else
            _etOnChange.postValue(false)

    }

    fun etTextWatcherOTP(text: String) {
        if (text.isNotEmpty() && text.length == 6)
            _etOnChangeOTP.postValue(true)
        else
            _etOnChangeOTP.postValue(false)
    }

    fun validation(domain: String, username: String, password: String) {
        when {
            domain.isEmpty() -> _validation.postValue("Please enter domain")
            username.isEmpty() -> _validation.postValue("Please enter username")
            password.isEmpty() -> _validation.postValue("Please enter password")
//            password.length < 8 -> _validation.postValue("Minimum password length should be 8 characters")
            else -> _validation.postValue("")
        }
    }

    fun validationOTP(otp: String) {
        when {
            otp.isEmpty() -> _validationOtp.postValue("Please enter OTP")
            otp.length < 6 -> _validationOtp.postValue("OTP length should be 6 characters")
            else -> _validationOtp.postValue("")
        }
    }



    fun occupancyCalendarApi(
        occupancyCalendarRequest: com.bitla.ts.phase2.dashboard_pojo.occupancyCalendarModel.request.ReqBody,
        apiType: String
    ) {


        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                dashboardRepository.newOccupancyCalendar(
                    occupancyCalendarRequest
                ) .collect {
                    when (it) {
                        is NetworkProcess.Loading -> {}
                        is NetworkProcess.Success -> {
                            _loadingState.postValue(LoadingState.LOADED)
                            _occupancyCalendarViewModel.postValue(
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
                Timber.d("occupancyCalendarException- ${e.message}")
            }
        }
    }









    fun storeFcmKey(
        apiKey: String,
        deviceId: String,
        fcmKey: String,
        apiType: String
    ) {


        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                dashboardRepository.newStoreFcmKey(
                    apiKey,
                    deviceId,
                    fcmKey
                ).collect {
                    when (it) {
                        is NetworkProcess.Loading -> {}
                        is NetworkProcess.Success -> {
                            _loadingState.postValue(LoadingState.LOADED)
                            _storeFcmViewModel.postValue(
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
                Timber.d("dashboardFetchException- ${e.message}")
            }
        }
    }

    fun getEndYYMMDD(startDate: String) {
        val next31Days = get31stDate(
            stringToDate(getDateDMY(startDate).toString(), DATE_FORMAT_D_M_Y) ?: Date()
        )
        _getDate.postValue(dateToString(next31Days, DATE_FORMAT_Y_M_D))
    }


    fun fetchCreditInfo(
        apiKey: String
    ) {
        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                dashboardRepository.fetchCreditInfo(
                    apiKey
                ).collect {
                    when (it) {
                        is NetworkProcess.Loading -> {}

                        is NetworkProcess.Success -> {
                            _loadingState.postValue(LoadingState.LOADED)
                            _creditInfoData.postValue(
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
                Timber.d("dashboardFetchException- ${e.message}")
            }
        }
    }
}
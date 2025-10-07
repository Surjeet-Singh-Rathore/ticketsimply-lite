package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitla.ts.domain.pojo.login_auth_post.request.LoginAuthPostRequest
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.login_with_otp.request.ReqBody
import com.bitla.ts.domain.pojo.logout_auth_post_req.LogoutReqBody
import com.bitla.ts.domain.repository.LoginRepository
import com.bitla.ts.koin.networkModule.NetworkProcess
import com.bitla.ts.utils.LoadingState
import com.bitla.ts.utils.security.EncrypDecryp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    companion object {
        val TAG: String = LoginViewModel::class.java.simpleName
    }

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    private val _data = MutableLiveData<LoginModel>()
    val data: LiveData<LoginModel>
        get() = _data

    private val _dataLogout = MutableLiveData<LoginModel>()
    val dataLogout: LiveData<LoginModel>
        get() = _dataLogout

    private val _etOnChange = MutableLiveData<Boolean>()
    val etOnChange: LiveData<Boolean>
        get() = _etOnChange

    private val _validation = MutableLiveData<String>()
    val validationData: LiveData<String>
        get() = _validation

    private val _dataLoginWithOtp = MutableLiveData<LoginModel>()
    val dataLoginWithOtp: LiveData<LoginModel>
        get() = _dataLoginWithOtp
    val messageSharedFlow = MutableSharedFlow<String>()


    fun loginApi(username: String, pass: String, locale: String?, deviceId: String, shiftId: Int? = null, counterId: Int?= null, counterBalance: String = "") {

        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            if(EncrypDecryp.isEncrypted()) {
                val loginAuthPostRequest = LoginAuthPostRequest(
                    login = EncrypDecryp.getEncryptedValue(username),
                    password = EncrypDecryp.getEncryptedValue(pass),
                    locale = locale,
                    device_id = EncrypDecryp.getEncryptedValue(deviceId),
                    is_encrypted = EncrypDecryp.isEncrypted(),
                    is_from_middle_tier = true,
                    shift_id = shiftId,
                    counter_id = counterId,
                    counter_balance = counterBalance
                )
                loginRepository.getNewLoginDetailsPost(loginAuthPostRequest)
                    .collect {
                        when (it) {
                            is NetworkProcess.Loading -> {}
                            is NetworkProcess.Success -> {
                                _data.postValue(
                                    it.data
                                )
                            }

                            is NetworkProcess.Failure -> {
                                messageSharedFlow.emit(it.message)
                            }
                        }
                    }
            } else {
                loginRepository.getNewLoginDetails(
                    username,
                    pass,
                    locale,
                    deviceId,
                    false,
                    shiftId,
                    counterId,
                    counterBalance
                ).collect {
                    when (it) {
                        is NetworkProcess.Loading -> {}
                        is NetworkProcess.Success -> {
                            _data.postValue(
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

    fun resetApi(login: String, pass: String, deviceId: String, shiftId: Int? = null, counterId: Int?= null, counterBalance: Double? = null) {

        val logOutReqBody=LogoutReqBody()
        logOutReqBody.login=EncrypDecryp.getEncryptedValue(login)
        logOutReqBody.password=EncrypDecryp.getEncryptedValue(pass)
        logOutReqBody.device_id=EncrypDecryp.getEncryptedValue(deviceId)
        logOutReqBody.is_encrypted=EncrypDecryp.isEncrypted()
        logOutReqBody.shift_id = shiftId
        logOutReqBody.counter_id = counterId
        logOutReqBody.counter_balance = counterBalance

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {


            if(EncrypDecryp.isEncrypted()) {
                loginRepository.getNewResetPostApi(logOutReqBody).collect {
                    when (it) {
                        is NetworkProcess.Loading -> {}
                        is NetworkProcess.Success -> {
                            _dataLogout.postValue(
                                it.data
                            )
                        }

                        is NetworkProcess.Failure -> {
                            messageSharedFlow.emit(it.message)
                        }
                    }
                }
            }else {
                loginRepository.getNewResetDetails(login, pass, deviceId, shiftId, counterId, counterBalance).collect {
                    when (it) {
                        is NetworkProcess.Loading -> {}
                        is NetworkProcess.Success -> {
                            _dataLogout.postValue(
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

    fun confirmOTP(
        loginWithOtpRequest: ReqBody
    ) {
        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            loginRepository.newGetLoginWithOTPDetails(
                loginWithOtpRequest
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _dataLoginWithOtp.postValue(
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

    fun etTextWatcher(username: String, password: String) {
        if (username.isNotEmpty() && password.isNotEmpty())
            _etOnChange.postValue(true)
        else
            _etOnChange.postValue(false)
    }

    fun etTextWatcher(text: String) {
        if (text.isNotEmpty())
            _etOnChange.postValue(true)
        else
            _etOnChange.postValue(false)
    }

    fun validation(username: String, password: String) {
        when {
            username.isEmpty() -> _validation.postValue("Please enter username")
            password.isEmpty() -> _validation.postValue("Please enter password")
            else -> _validation.postValue("")
        }
    }

    fun validationOTP(otp: String) {
        when {
            otp.isEmpty() -> _validation.postValue("Please enter OTP")
            otp.length < 6 -> _validation.postValue("OTP length should be 6 characters")
            else -> _validation.postValue("")
        }
    }
}
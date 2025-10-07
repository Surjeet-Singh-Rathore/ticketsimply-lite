package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitla.ts.domain.pojo.confirm_reset_password.ConfirmResetPasswordModel
import com.bitla.ts.domain.pojo.reset_password_with_otp.ResetPasswordWithOtp
import com.bitla.ts.domain.pojo.reset_password_with_otp.request.ReqBody
import com.bitla.ts.domain.repository.ResetPasswordRepository
import com.bitla.ts.koin.networkModule.NetworkProcess
import com.bitla.ts.utils.LoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

open class ResetPasswordViewModel<T : Any?>(private val resetPasswordRepository: ResetPasswordRepository) :
    ViewModel() {

    companion object {
        val tag: String = ResetPasswordViewModel::class.java.simpleName
    }

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    private val _resetPasswordWithOtp = MutableLiveData<ResetPasswordWithOtp>()
    val resetPasswordWithOtp: LiveData<ResetPasswordWithOtp>
        get() = _resetPasswordWithOtp

    private val _confirmResetPassword = MutableLiveData<ConfirmResetPasswordModel>()
    val confirmResetPassword: LiveData<ConfirmResetPasswordModel>
        get() = _confirmResetPassword

    private val _validation = MutableLiveData<String>()
    val validationData: LiveData<String>
        get() = _validation

    private var apiType: String? = null

    val messageSharedFlow = MutableSharedFlow<String>()


    /*  fun resetPasswordWithOtpApi(
          authorization: String,
          apiKey: String,
          request: ResetPasswordRequest,
          apiType: String
      ) {

          _loadingState.postValue(LoadingState.LOADING)

          viewModelScope.launch(Dispatchers.IO) {
              _resetPasswordWithOtp.postValue(
                  resetPasswordRepository.resetPasswordApi(
                      authorization,
                      apiKey,
                      request
                  ).body()
              )
          }
      }*/

    fun resetPasswordWithOtpApi(
        request: ReqBody,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            resetPasswordRepository.newResetPasswordApi(
                request
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _resetPasswordWithOtp.postValue(
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

    /* fun confirmResetPasswordApi(
         authorization: String,
         apiKey: String,
         confirmResetPasswordRequest: ConfirmResetPasswordRequest,
         apiType: String
     ) {

         _loadingState.postValue(LoadingState.LOADING)

         viewModelScope.launch(Dispatchers.IO) {
             _confirmResetPassword.postValue(
                 resetPasswordRepository.confirmResetPasswordApi(
                     authorization,
                     apiKey,
                     confirmResetPasswordRequest
                 ).body()
             )
         }
     }  */

    fun confirmResetPasswordApi(
        confirmResetPasswordRequest: com.bitla.ts.domain.pojo.confirm_reset_password.request.ReqBody,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            resetPasswordRepository.newConfirmResetPasswordApi(
                confirmResetPasswordRequest
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _confirmResetPassword.postValue(
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

    fun validation(password: String, confirmPassword: String) {
        when {
            password.isEmpty() -> _validation.postValue("Please enter password")
//            password.length < 8 -> _validation.postValue("Minimum password length should be 8 characters")
            confirmPassword.isEmpty() -> _validation.postValue("Please enter Confirm Password")
            password != confirmPassword -> _validation.postValue("Password & Confirm Password should be same")
            else -> _validation.postValue("")
        }
    }
}
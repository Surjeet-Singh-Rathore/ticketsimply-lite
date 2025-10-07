package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bitla.ts.domain.pojo.forgot_model.ForgotModel
import com.bitla.ts.domain.repository.ForgotPasswordRepository
import com.bitla.ts.utils.LoadingState

class ForgotPasswordViewModel(private val forgotPasswordRepository: ForgotPasswordRepository) :
    ViewModel() {
    companion object {
        val TAG: String = ForgotPasswordViewModel::class.java.simpleName
    }

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    private val _data = MutableLiveData<ForgotModel>()
    val data: LiveData<ForgotModel>
        get() = _data

    private val _validation = MutableLiveData<String>()
    val validationData: LiveData<String>
        get() = _validation

    fun validation(mobileNumber: String) {
        if (mobileNumber.isEmpty())
            _validation.postValue("Please enter Mobile Number")
        else if (mobileNumber.length < 10)
            _validation.postValue("Mobile Number length should be 10 characters")
        else
            _validation.postValue("")
    }
}
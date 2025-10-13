package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitla.ts.domain.pojo.mealCoupon.RestaurantListResponse
import com.bitla.ts.domain.pojo.reports.ReportsResponse
import com.bitla.ts.domain.pojo.self_audit_data.SelfAuditResponse
import com.bitla.ts.domain.pojo.self_audit_question.response.SelfAuditQuestionResponse
import com.bitla.ts.domain.pojo.submit_self_audit_form.request.SubmitSelfAuditFormRequest
import com.bitla.ts.domain.pojo.submit_self_audit_form.response.SubmitSelfAuditFormResponse
import com.bitla.ts.domain.repository.RestaurantRepository
import com.bitla.ts.domain.repository.SelfAuditRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SelfAuditViewModel(private val selfAuditRepository: SelfAuditRepository) : ViewModel() {



    private val _selfAuditListResponse = MutableLiveData<SelfAuditQuestionResponse>()
    val selfAuditListResponse: LiveData<SelfAuditQuestionResponse>
        get() = _selfAuditListResponse

    fun getSelfAuditListApi(
        apiKey: String,
        resId:String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _selfAuditListResponse.postValue(
                selfAuditRepository.getSelfAuditQuestions(
                    apiKey,
                    resId
                ).body()
            )
        }
    }


    private val _selfAuditFormSubmitResponse = MutableLiveData<SubmitSelfAuditFormResponse>()
    val selfAuditFormSubmitResponse: LiveData<SubmitSelfAuditFormResponse>
        get() = _selfAuditFormSubmitResponse

    fun selfAuditFormApi(
        selfAuditFormSubmitRequest: SubmitSelfAuditFormRequest
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _selfAuditFormSubmitResponse.postValue(
                selfAuditRepository.selfAuditFormSubmit(
                   selfAuditFormSubmitRequest
                ).body()
            )
        }
    }
}
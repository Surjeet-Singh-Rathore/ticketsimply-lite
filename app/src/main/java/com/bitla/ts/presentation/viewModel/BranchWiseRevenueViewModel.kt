package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitla.ts.databinding.DashboardBranchWiseRevenuePopupBinding
import com.bitla.ts.domain.pojo.dashboard_branchwise_revenue_popup.BranchWiseRevenuePopUpResponse
import com.bitla.ts.domain.pojo.stage_summary_details.StageSummaryModel
import com.bitla.ts.domain.repository.BranchWiseRevenueRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BranchWiseRevenueViewModel<T : Any?>(private val branchWiseRevenueRepository: BranchWiseRevenueRepository) : ViewModel(){
    private val _branchWiseRevenueDetails = MutableLiveData<BranchWiseRevenuePopUpResponse>()
    val branchWiseRevenueDetails: LiveData<BranchWiseRevenuePopUpResponse>
        get() = _branchWiseRevenueDetails

    fun branchWiseRevenueDetailsApi(
        apiKey: String,
        branchId : String,
        fromDate: String,
        toDate : String
    ) {

        viewModelScope.launch(Dispatchers.IO) {
            _branchWiseRevenueDetails.postValue(
                branchWiseRevenueRepository.newBranchWiseRevenueDetails(
                    apiKey,
                    branchId,
                    fromDate,
                    toDate
                ).body()
            )
        }
    }
}
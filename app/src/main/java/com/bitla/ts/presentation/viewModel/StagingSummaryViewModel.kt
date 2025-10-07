package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitla.ts.domain.pojo.stage_summary_details.StageSummaryModel
import com.bitla.ts.domain.repository.StagingSummaryRepository
import com.bitla.ts.utils.LoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class StagingSummaryViewModel<T : Any?>(private val stagingSummaryRepository: StagingSummaryRepository) : ViewModel(){
    private val _viewBookingSummaryResponse = MutableLiveData<StageSummaryModel>()
    val stagingsummaryDetails: LiveData<StageSummaryModel>
        get() = _viewBookingSummaryResponse

    fun StagingSummaryDetailsAPI(
        apiKey: String,
        resId: String,
    ) {

        viewModelScope.launch(Dispatchers.IO) {
            _viewBookingSummaryResponse.postValue(
                stagingSummaryRepository.newStagingSummaryDetailsService(
                    apiKey,
                    resId,
                ).body()
            )
        }
    }
}
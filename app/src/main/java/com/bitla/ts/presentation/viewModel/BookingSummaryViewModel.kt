package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bitla.ts.domain.repository.BookingSummaryRepository
import com.bitla.ts.utils.LoadingState
import kotlinx.coroutines.Dispatchers
import androidx.lifecycle.viewModelScope
import com.bitla.ts.domain.pojo.booking_summary_details.BookingSummaryResponse

import kotlinx.coroutines.launch


class BookingSummaryViewModel<T : Any?>(private val bookingSummaryRepository: BookingSummaryRepository) :
    ViewModel() {
    private val _bookingSummaryDetails = MutableLiveData<BookingSummaryResponse>()
    val bookingSummaryDetails: LiveData<BookingSummaryResponse>
        get() = _bookingSummaryDetails


    fun bookingSummaryDetailsAPI(
        apiKey: String,
        resId: String,
    ) {

        viewModelScope.launch(Dispatchers.IO) {
            _bookingSummaryDetails.postValue(
                bookingSummaryRepository.newBookingSummaryDetailsService(
                    apiKey,
                    resId,
                ).body()
            )
        }
    }
}
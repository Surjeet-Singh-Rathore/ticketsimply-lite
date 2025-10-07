package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitla.ts.domain.pojo.blacklist_number.BlackListNumberResponse
import com.bitla.ts.domain.pojo.blocked_numbers_list.BlockedNumbersListResponse
import com.bitla.ts.domain.repository.BlackListRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BlackListViewModel<T : Any?>(private val blacklistRepository: BlackListRepository) : ViewModel() {
    private val _blackListDetails = MutableLiveData<BlackListNumberResponse>()
    val blackListDetails: LiveData<BlackListNumberResponse>
        get() = _blackListDetails

    private val _blackListNumberListDetails = MutableLiveData<BlockedNumbersListResponse>()
    val blackListNumberListDetails: LiveData<BlockedNumbersListResponse>
        get() = _blackListNumberListDetails

    fun blackListNumberApi(
        apiKey: String,
        phoneNumber:String,
        locale:String,
        remarks:String,
        status:String
    ) {


        viewModelScope.launch(Dispatchers.IO) {
            _blackListDetails.postValue(
                blacklistRepository.newBlackListNumber(
                    apiKey,
                    phoneNumber,
                    locale,
                    remarks,
                    status
                ).body()
            )
        }
    }

    fun blockedNumbersList(
        apiKey: String,
        locale:String
    ) {


        viewModelScope.launch(Dispatchers.IO) {
            _blackListNumberListDetails.postValue(
                blacklistRepository.newBlockedNumbersList(
                    apiKey,
                    locale
                ).body()
            )
        }
    }
}
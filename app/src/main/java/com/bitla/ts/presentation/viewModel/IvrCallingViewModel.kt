package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitla.ts.domain.pojo.ivr_call.IvrCallRequest
import com.bitla.ts.domain.pojo.ivr_call.IvrCallResponse
import com.bitla.ts.domain.repository.IvrCallingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class IvrCallingViewModel <T : Any?>(private val ivrCallingRepository: IvrCallingRepository) : ViewModel(){

        private val _ivrCallingDetails = MutableLiveData<IvrCallResponse>()
        val ivrCallingDetails: LiveData<IvrCallResponse>
            get() = _ivrCallingDetails

        fun ivrCallingDetailsApi(
//            resId: String,
//            apiKey: String,
//            boardingId:String,
//            option: String
            ivrCallRequest: IvrCallRequest
        ) {
            viewModelScope.launch(Dispatchers.IO) {
                _ivrCallingDetails.postValue(
                    ivrCallingRepository.newIvrCall(
//                        resId,
//                        apiKey,
//                        boardingId,
//                        option
                        ivrCallRequest
                    ).body()
                )
            }
        }

}
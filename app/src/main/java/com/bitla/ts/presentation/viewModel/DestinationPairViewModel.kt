package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bitla.ts.domain.repository.AvailableRoutesRepository
import com.bitla.ts.utils.LoadingState


class DestinationPairViewModel(private val availableRoutesRepository: AvailableRoutesRepository) :
    ViewModel() {

    companion object {
        val TAG: String = DestinationPairViewModel::class.java.simpleName
    }

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

}


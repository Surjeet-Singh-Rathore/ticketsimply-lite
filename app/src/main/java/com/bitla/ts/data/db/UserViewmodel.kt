package com.bitla.ts.data.db

import androidx.lifecycle.*
import com.bitla.ts.domain.pojo.user.*
import com.bitla.ts.utils.sharedPref.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.*
import javax.inject.*

@HiltViewModel
class UserViewModel @Inject constructor(private val userRepositoryImpl: UserRepositoryImpl): ViewModel() {

    private val _restartActivity = MutableLiveData<Boolean>()
    val restartActivity: LiveData<Boolean>
        get() = _restartActivity

    private val _getAllUsers = MutableLiveData<List<User>>()
    val getAllUsers: LiveData<List<User>>
        get() = _getAllUsers

    private val _getCurrentUser = MutableLiveData<User?>()
    val getCurrentUser: LiveData<User?>
        get() = _getCurrentUser

    fun insertUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            user.currentTimeStamp = System.currentTimeMillis()
            PreferenceUtils.setPreference(PREF_LOGO, user.logoUrl) ?: ""
            userRepositoryImpl.insertUser(user)
                .flowOn(Dispatchers.IO)
                .catch {
                }.collect{
                }
        }
    }

    fun insertUserAndRestartActivity(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            user.currentTimeStamp = System.currentTimeMillis()
            PreferenceUtils.setPreference(PREF_LOGO, user.logoUrl) ?: ""
            userRepositoryImpl.insertUser(user)
                .flowOn(Dispatchers.IO)
                .catch {
                    _restartActivity.postValue(false)
                }.collect{
                    _restartActivity.postValue(true)
                }
        }
    }

    fun getAllUsers() {
        viewModelScope.launch {
            userRepositoryImpl.getAllUsers()
                .flowOn(Dispatchers.IO)
                .catch {
                    Timber.d("Error in getAllUsers ViewModel"+it.message)
                    _getAllUsers.postValue(mutableListOf())
                }
                .collect {
                    _getAllUsers.postValue(it)
                }
        }
    }

    fun getCurrentUser() {
        viewModelScope.launch {
            userRepositoryImpl.getCurrentUser()
                .flowOn(Dispatchers.IO)
                .catch {
                    Timber.d("Error in getCurrentUser ViewModel"+it.message)
                    _getCurrentUser.postValue(null)
                }
                .collect {
                    _getCurrentUser.postValue(it)
                }
        }
    }

    fun deleteUserAndRestartActivity(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepositoryImpl.deleteUser(user)
                .flowOn(Dispatchers.IO)
                .catch {
                    _restartActivity.postValue(false)
                }.collect{
                    _restartActivity.postValue(true)
                }
        }
    }
}
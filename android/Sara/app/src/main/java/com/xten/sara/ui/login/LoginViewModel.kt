package com.xten.sara.ui.login

import android.content.SharedPreferences
import androidx.lifecycle.*
import com.xten.sara.data.repository.SaraServiceRepository
import com.xten.sara.util.LoginUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-03-30
 * @desc
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val saraServiceRepository: SaraServiceRepository,
    private val prefs: SharedPreferences
) : ViewModel() {

    val autoLogin = MutableLiveData(false)

    private val _state = MutableLiveData(State.NONE)
    val state: LiveData<State> = _state

    fun requestLogin(email: String?) {
        email?.let {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val result = saraServiceRepository.login(it).body()
                    handleLoginResult(result?.token)
                } catch (e: Exception) {
                    handleLoginResult(null)
                }
                cancel()
            }
        }
    }

    private fun handleLoginResult(token: String?) = token?.let {
        if(autoLogin.value!!) LoginUtils.saveToken(prefs, it)
        _state.postValue(State.SUCCESS)
    } ?: _state.postValue(State.FAIL)

}

enum class State {
    NONE, SUCCESS, FAIL
}
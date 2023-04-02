package com.xten.sara.ui.login

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xten.sara.data.SaraServiceRepository
import com.xten.sara.util.LoginUtils
import com.xten.sara.util.constants.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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

    fun requestLogin(email: String?, nickName: String?, profile: String?) {
        email?.let {
            viewModelScope.launch {
                val token = saraServiceRepository.downloadToken(email, nickName, profile)
                handleLoginResult(token)
            }
        }
    }

    private fun handleLoginResult(token: String?) {
        if(token == null) {
            _state.postValue(State.FAIL)
            return
        }
        LoginUtils.setLoginState(prefs, autoLogin.value!!)
        LoginUtils.saveToken(prefs, token)
        _state.postValue(State.SUCCESS)
    }

}
package com.xten.sara.ui.login

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.Resource
import com.example.common.State
import com.xten.sara.data.SaraServiceRepository
import com.xten.sara.util.LoginUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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

    private val _loginState = MutableLiveData(State.NONE)
    val loginState: LiveData<State> = _loginState

    fun setLoginState(state: State) {
        _loginState.postValue(state)
    }

    fun requestLogin(email: String?, nickName: String?, profile: String?) {
        email?.let {
            saraServiceRepository.downloadToken(email, nickName, profile).onEach { result ->
                setLoginState(result.state)
                if(result is Resource.Success) {
                    val token = result.data?.token
                    token?.let {
                        LoginUtils.setLoginState(prefs, autoLogin.value!!)
                        LoginUtils.saveToken(prefs, token)
                        setLoginState(result.state)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

}
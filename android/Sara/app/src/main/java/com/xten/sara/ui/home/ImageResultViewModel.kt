package com.xten.sara.ui.home

import android.net.Uri
import androidx.lifecycle.*
import com.example.common.QueryType
import com.example.common.*
import com.xten.sara.data.SaraServiceRepository
import com.xten.sara.data.model.ChatGPT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.io.File
import javax.inject.Inject

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-03-28
 * @desc
 */
@HiltViewModel
class ImageResultViewModel @Inject constructor(
    private val saraServiceRepository: SaraServiceRepository
) : ViewModel() {

    val freeText = MutableLiveData<String?>()

    private val _loadingState = MutableLiveData(State.NONE)
    val loadingState : LiveData<State> get() = _loadingState

    private fun setLoadingState(state: State) {
        _loadingState.postValue(state)
    }
    
    private var requestGetImageUrlCoroutine: Job? = null
    fun requestImageAnalysis(path: String, type: Int) {
        requestGetImageUrlCoroutine = viewModelScope.launch {
            val image = File(path)
            saraServiceRepository.downloadImageUrl(image).onEach { result ->
                setLoadingState(result.state)
                if(result is Resource.Success) {
                    photoUrl = result.data?.photoUrl
                    requestChatGPT(type)
                }
            }.launchIn(this)
        }
    }

    private var photoUrl: String? = null

    private var requestChatGPTCoroutine: Job? = null
    fun requestChatGPT(type: Int) = photoUrl?.let {
        requestChatGPTCoroutine = viewModelScope.launch {
            saraServiceRepository.downloadResultChatGPT(
                it,
                type,
                if(type == QueryType.FREE.type()) freeText.value else null
            ).onEach { result ->
                setLoadingState(result.state)
                if(result is Resource.Success) {
                    _resultAnalysis.postValue(result.data)
                }
            }.launchIn(this)
        }
    }

    private val _resultAnalysis = MutableLiveData<ChatGPT?>()
    val resultAnalysis: LiveData<ChatGPT?> = _resultAnalysis


    private val _saveResult = MutableLiveData<State>()
    val saveResult: LiveData<State> = _saveResult

    fun requestSaveContent(text: String, type: Int){
        resultAnalysis.value?.run {
            saraServiceRepository.requestSaveContent(
                photoUrl = photoUrl!!,
                title = freeText.value!!,
                text = text,
                type = type
            ).onEach {
                _saveResult.postValue(it.state)
            }.launchIn(viewModelScope)
        }
    }

    fun cancelRequest() {
        requestGetImageUrlCoroutine?.cancel()
        requestChatGPTCoroutine?.cancel()
    }

}


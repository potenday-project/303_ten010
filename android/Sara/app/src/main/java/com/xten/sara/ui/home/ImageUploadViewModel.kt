package com.xten.sara.ui.home

import android.net.Uri
import androidx.lifecycle.*
import com.example.common.QueryType
import com.example.common.*
import com.xten.sara.data.SaraServiceRepository
import com.xten.sara.data.model.ChatGPT
import com.xten.sara.data.model.Image
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
class ImageUploadViewModel @Inject constructor(
    private val saraServiceRepository: SaraServiceRepository
) : ViewModel() {

    private val _imageUri = MutableLiveData<Uri?>()
    val imageUri: LiveData<Uri?> = _imageUri
    
    fun setImageUri(uri: Uri) {
        _imageUri.value = uri
    }

    private val _queryType = MutableLiveData(QueryType.ESSAY)
    val queryType: LiveData<QueryType> = _queryType

    val freeText = MutableLiveData<String?>()

    fun setQueryType(num: Int) {
        _queryType.value = when (num) {
            TYPE_1 -> QueryType.ESSAY
            TYPE_2 -> QueryType.POEM
            TYPE_3 -> QueryType.EVALUATION
            else -> QueryType.FREE
        }
    }
    private fun getCurQueryType() : Int {
        return queryType.value!!.type()
    }

    private val _loadingState = MutableLiveData(State.NONE)
    val loadingState : LiveData<State> get() = _loadingState

    private fun setLoadingState(state: State) {
        _loadingState.postValue(state)
    }
    fun getCurLoadingState() = loadingState.value!!
    
    private var requestGetImageUrlCoroutine: Job? = null
    fun requestImageAnalysis(path: String) {
        requestGetImageUrlCoroutine = viewModelScope.launch {
            val image = File(path)
            saraServiceRepository.downloadImageUrl(image).onEach { result ->
                setLoadingState(result.state)
                if(result is Resource.Success) {
                    photoUrl = result.data?.photoUrl
                    requestChatGPT()
                }
            }.launchIn(this)
        }
    }

    private var photoUrl: String? = null

    private var requestChatGPTCoroutine: Job? = null
    fun requestChatGPT() = photoUrl?.let {
        requestChatGPTCoroutine = viewModelScope.launch {
            saraServiceRepository.downloadResultChatGPT(
                it,
                getCurQueryType(),
                if(queryType.value == com.example.common.QueryType.FREE) freeText.value else null
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
    fun requestSaveContent(text: String){
        resultAnalysis.value?.run {
            saraServiceRepository.requestSaveContent(
                photoUrl = photoUrl!!,
                title = freeText.value!!,
                text = text,
                type = getCurQueryType()
            ).onEach {
                _saveResult.postValue(it.state)
            }.launchIn(viewModelScope)
        }
    }

    fun cancelRequest() {
        requestGetImageUrlCoroutine?.cancel()
        requestChatGPTCoroutine?.cancel()
    }

    fun initFreeText() {
        freeText.value = null
    }

    fun initQueryType() {
        _queryType.value = com.example.common.QueryType.ESSAY
    }

    fun initViewModel() {
        _saveResult.value = State.NONE
        _imageUri.value = null
        _loadingState.value = State.NONE
        photoUrl = null
        _resultAnalysis.value = null
        initFreeText()
    }

}


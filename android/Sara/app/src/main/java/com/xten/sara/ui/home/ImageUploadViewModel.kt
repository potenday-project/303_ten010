package com.xten.sara.ui.home

import android.net.Uri
import androidx.lifecycle.*
import com.xten.sara.data.SaraServiceRepository
import com.xten.sara.data.ChatGPT
import com.xten.sara.data.Image
import com.xten.sara.util.constants.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
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

    private val _state = MutableLiveData(State.NONE)
    val state : LiveData<State> get() = _state

    fun setState(state: State) {
        _state.postValue(state)
    }
    fun getCurState() = state.value!!
    
    private var requestGetImageUrlCoroutine: Job? = null
    fun requestImageAnalysis(path: String) {
        requestGetImageUrlCoroutine = viewModelScope.launch {
            setState(State.ING)
            val image = File(path)
            val result = saraServiceRepository.downloadImageUrl(image)
            handleRequestGetImageUrlResult(result)
        }
    }

    private var photoUrl: String? = null
    private fun handleRequestGetImageUrlResult(result: Image?) = result?.let {
        photoUrl = it.photoUrl
        requestChatGPT()
    } ?: setState(State.FAIL)

    private var requestChatGPTCoroutine: Job? = null
    fun requestChatGPT() = photoUrl?.let {
        requestChatGPTCoroutine = viewModelScope.launch {
            val result = saraServiceRepository.downloadResultChatGPT(
                it,
                getCurQueryType(),
                if(queryType.value == QueryType.FREE) freeText.value else null
            )
            handleRequestChatGPTResult(result)
        }
    }

    private val _resultAnalysis = MutableLiveData<ChatGPT?>()
    val resultAnalysis: LiveData<ChatGPT?> = _resultAnalysis

    private var text: String? = null
    private fun handleRequestChatGPTResult(result: ChatGPT?) = result?.let {
        setState(State.SUCCESS)
        text = result.text
        _resultAnalysis.value = it
    } ?: setState(State.FAIL)


    private val _saveResult = MutableLiveData<String?>()
    val saveResult: LiveData<String?> = _saveResult
    fun saveContent() = viewModelScope.launch {
        resultAnalysis.value?.run {
            val result = saraServiceRepository.requestSaveContent(
                photoUrl = photoUrl!!,
                title = freeText.value!!,
                text = text,
                type = getCurQueryType()
            )
            _saveResult.value = result
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
        _queryType.value = QueryType.ESSAY
    }

    fun initViewModel() {
        _saveResult.value = null
        _imageUri.value = null
       // _queryType.value = QueryType.ESSAY
        _state.value = State.NONE
        photoUrl = null
        _resultAnalysis.value = null
        text = null
        _saveResult.value = null
        initFreeText()
    }

}


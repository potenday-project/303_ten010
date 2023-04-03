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

    private val _loadingState = MutableLiveData(State.NONE)
    val loadingState : LiveData<State> get() = _loadingState
    fun setLoadingState(state: State) {
        _loadingState.postValue(state)
    }
    fun getCurLoadingState() = loadingState.value!!
    
    private var requestGetImageUrlCoroutine: Job? = null
    fun requestImageAnalysis(path: String) {
        requestGetImageUrlCoroutine = viewModelScope.launch {
            setLoadingState(State.ING)
            val image = File(path)
            val result = saraServiceRepository.downloadImageUrl(image)
            handleRequestGetImageUrlResult(result)
        }
    }

    private var photoUrl: String? = null
    private fun handleRequestGetImageUrlResult(result: Image?) = result?.let {
        photoUrl = it.photoUrl
        requestChatGPT()
    } ?: setLoadingState(State.FAIL)

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

    private fun handleRequestChatGPTResult(result: ChatGPT?) = result?.let {
        _resultAnalysis.postValue(it)
        setLoadingState(State.SUCCESS)
    } ?: setLoadingState(State.FAIL)


    private val _saveResult = MutableLiveData<String?>()
    val saveResult: LiveData<String?> = _saveResult
    fun requestSaveContent(text: String) = viewModelScope.launch {
        resultAnalysis.value?.run {
            val result = saraServiceRepository.requestSaveContent(
                photoUrl = photoUrl!!,
                title = freeText.value!!,
                text = text,
                type = getCurQueryType()
            )
            _saveResult.postValue(result)
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
        _loadingState.value = State.NONE
        photoUrl = null
        _resultAnalysis.value = null
        _saveResult.value = null
        initFreeText()
    }

}


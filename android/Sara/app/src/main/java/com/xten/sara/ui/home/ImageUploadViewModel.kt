package com.xten.sara.ui.home

import android.net.Uri
import androidx.lifecycle.*
import com.xten.sara.data.SaraServiceRepository
import com.xten.sara.data.ChatGPT
import com.xten.sara.data.Image
import com.xten.sara.util.*
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
    val imageUri : LiveData<Uri?> = _imageUri
    fun setImageUri(uri: Uri?) {
        _imageUri.value = uri
    }

    private val _queryType = MutableLiveData(QueryType.ESSAY)
    val queryType : LiveData<QueryType> = _queryType

    val freeText = MutableLiveData("")

    fun setQueryType(num: Int) {
        _queryType.value = when(num) {
            TYPE_1 -> QueryType.ESSAY
            TYPE_2 -> QueryType.POEM
            TYPE_3 -> QueryType.EVALUATION
            else -> QueryType.FREE
        }
    }
    private fun getCurQueryType() = queryType.value!!.type()

    private val _state = MutableLiveData(State.NONE)
    val state : LiveData<State> = _state

    fun setState(state: State) {
        _state.value = state
    }

    fun requestImageAnalysis(uri: String) = viewModelScope.launch {
        val image = File(uri)
        val result = saraServiceRepository.getImageUrl(image)
        handleRequestGetImageUrlResult(result)
    }

    private var photoUrl : String? = null
    private fun handleRequestGetImageUrlResult(result: Image?) = result?.let {
        viewModelScope.launch {
            photoUrl = it.photoUrl
            requestChatGPT()
        }
    } ?: _state.postValue(State.FAIL)

    fun requestChatGPT() = photoUrl?.let {
        viewModelScope.launch {
            val result = saraServiceRepository.requestChatGPT(
                it,
                getCurQueryType()
            )
            handleRequestChatGPTResult(result)
        }
    } ?: _state.postValue(State.FAIL)

    private val _resultAnalysis = MutableLiveData<ChatGPT>()
    val resultAnalysis : LiveData<ChatGPT> = _resultAnalysis
    private fun handleRequestChatGPTResult(result: ChatGPT?) = result?.let {
        _resultAnalysis.postValue(it)
        _state.postValue(State.SUCCESS)
    } ?: _state.postValue(State.FAIL)


    private val _saveResult = MutableLiveData<String>()
    val saveResult : LiveData<String> = _saveResult
    fun saveContent() = viewModelScope.launch {
        resultAnalysis.value?.run {
            val result = saraServiceRepository.saveContent(
                photoUrl!!,
                text
            )
            _saveResult.postValue(result)
        }
    }

}


package com.xten.sara.ui.home

import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.xten.sara.data.SaraServiceRepository
import com.xten.sara.data.response.ChatGPTResponse
import com.xten.sara.data.response.ImageResponse
import com.xten.sara.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.NonCancellable.cancel
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
    private fun handleRequestGetImageUrlResult(result: ImageResponse?) = result?.let {
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

    private val _resultAnalysis = MutableLiveData<ChatGPTResponse>()
    val resultAnalysis : LiveData<ChatGPTResponse> = _resultAnalysis
    private fun handleRequestChatGPTResult(result: ChatGPTResponse?) = result?.let {
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

enum class QueryType {
    ESSAY {
        override fun desc(): String ="이 사진으로 줄글 생성하기"
        override fun type(): Int = 1
    },
    POEM {
        override fun desc(): String ="이 사진으로 시 생성하기"
        override fun type(): Int = 2
    },
    EVALUATION {
        override fun desc(): String ="이 사진으로 평가받기"
        override fun type(): Int = 3
    },
    FREE {
        override fun desc(): String ="이 사진으로 요청하기"
        override fun type(): Int = 4
    };
    abstract fun desc() : String
    abstract fun type() : Int
}

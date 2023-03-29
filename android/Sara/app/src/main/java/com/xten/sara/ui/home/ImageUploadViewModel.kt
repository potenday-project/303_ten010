package com.xten.sara.ui.home

import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.xten.sara.data.repository.ImageTaggingServiceRepository
import com.xten.sara.data.response.ImageTaggingResponse
import com.xten.sara.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
    private val imageTaggingServiceRepository: ImageTaggingServiceRepository
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
    
    fun requestImageAnalysis(uri: String) = viewModelScope.launch(Dispatchers.IO) {
        _state.postValue(false)
        try {
            val image = File(uri)
            val result = imageTaggingServiceRepository.getUploadId(image)
            Log.e(TAG, "requestImageAnalysis: ${result.body()}", )
            if(result.isSuccessful) {
                val uploadId = result.body()!!.result.uploadId
                Log.e(TAG, "requestImageAnalysis: $uploadId", )
            }
        } catch (e: Exception) {
            Log.e(TAG, "requestImageAnalysis: $e", )
        }
        _state.postValue(true)
    }

    private val _state = MutableLiveData(false)
    val state : LiveData<Boolean> = _state

}

enum class QueryType {
    ESSAY {
        override fun desc(): String ="이 사진으로 줄글 생성하기"
        override fun query(): String ="사진에 대한 짧은 글을 써줘"
    },
    POEM {
        override fun desc(): String ="이 사진으로 시 생성하기"
        override fun query(): String ="사진에 대한 시를 써줘"
    },
    EVALUATION {
        override fun desc(): String ="이 사진으로 평가받기"
        override fun query(): String ="사진에 대한 평가를 부탁할게"
    },
    FREE {
        override fun desc(): String ="이 사진으로 요청하기"
        override fun query(): String =""
    };
    abstract fun desc() : String
    abstract fun query() : String
}

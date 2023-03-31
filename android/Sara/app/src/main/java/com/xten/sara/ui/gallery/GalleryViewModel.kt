package com.xten.sara.ui.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xten.sara.data.SaraServiceRepository
import com.xten.sara.data.Gallery
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
class GalleryViewModel @Inject constructor(
    private val saraServiceRepository: SaraServiceRepository
) : ViewModel() {

    private val _galleryList = MutableLiveData<List<Gallery>>()
    val galleryList: LiveData<List<Gallery>> = _galleryList

    fun updateGallery(email: String? = null) = viewModelScope.launch {
        saraServiceRepository.run {
            _galleryList.postValue(
                email?.run { downloadMyCollection() } ?: downloadCollection()
            )
        }
    }

    private val _deleteResult = MutableLiveData<String>()
    val deleteResult: LiveData<String> = _deleteResult
    fun deleteContent(id: String) = viewModelScope.launch {
        val result = saraServiceRepository.requestDeleteContent(id)
        _deleteResult.postValue(result)
    }

}
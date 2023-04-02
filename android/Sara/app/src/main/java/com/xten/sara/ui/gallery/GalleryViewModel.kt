package com.xten.sara.ui.gallery

import androidx.lifecycle.*
import com.xten.sara.data.Gallery
import com.xten.sara.data.SaraServiceRepository
import com.xten.sara.util.constants.QueryType
import com.xten.sara.util.constants.TYPE_1
import com.xten.sara.util.constants.TYPE_2
import com.xten.sara.util.constants.TYPE_3
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

    private val _galleryList = MutableLiveData<List<Gallery>?>()
    val galleryList: LiveData<List<Gallery>?> = _galleryList

    private var defaultList: List<Gallery>? = listOf()

    fun updateGallery(email: String? = null) = viewModelScope.launch {
        saraServiceRepository.run {
            email?.let {
                defaultList = downloadMyCollection()
                _galleryList.postValue(defaultList)
            } ?: let {
                defaultList = downloadCollection()
                _galleryList.postValue(defaultList)
            }
        }
    }

    private val _deleteResult = MutableLiveData<String>()
    val deleteResult: LiveData<String> = _deleteResult

    val input = MutableLiveData<String?>()
    fun getCurInput() = input.value ?: ""
    fun requestSearch() = defaultList?.let {
        val param = input.value!!.trim()
        _galleryList.value = it.filter { gallery ->
            gallery.title?.let { title ->
                if(title.contains(param)) return@filter true
            }
            gallery.text?.let { text ->
                if(text.contains(param)) return@filter true
            }
            gallery.type?.let { type ->
                val name = when(type) {
                    TYPE_1 -> QueryType.ESSAY.str()
                    TYPE_2 -> QueryType.POEM.str()
                    TYPE_3 -> QueryType.EVALUATION.str()
                    else -> QueryType.FREE.str()
                }
                if(param == name) return@filter true
            }
            false
        }
    }

    fun resetGallery() {
        _galleryList.value = defaultList
    }

    fun deleteContent(id: String) = viewModelScope.launch {
        val result = saraServiceRepository.requestDeleteContent(id)
        _deleteResult.postValue(result)
    }

}
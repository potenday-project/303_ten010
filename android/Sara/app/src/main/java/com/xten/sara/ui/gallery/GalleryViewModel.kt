package com.xten.sara.ui.gallery

import androidx.lifecycle.*
import com.example.common.*
import com.xten.sara.data.SaraServiceRepository
import com.xten.sara.data.model.Gallery
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
class GalleryViewModel @Inject constructor(
    private val saraServiceRepository: SaraServiceRepository
) : ViewModel() {

    private val _galleryList = MutableLiveData<List<Gallery>?>()
    val galleryList: LiveData<List<Gallery>?> = _galleryList

    private var defaultList: List<Gallery>? = listOf()

    fun updateGallery(email: String? = null) {
        email?.let {
            saraServiceRepository.downloadMyCollection().onEach { result ->
                if(result is Resource.Success) {
                    defaultList = result.data
                    _galleryList.postValue(defaultList)
                }
            }.launchIn(viewModelScope)
        }
    }

    private val _deleteResult = MutableLiveData<State>()
    val deleteResult: LiveData<State> = _deleteResult

    fun requestSearch(param: String) = defaultList?.let {
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

    fun deleteContent(id: String) {
        saraServiceRepository.requestDeleteContent(id).onEach { result ->
            _deleteResult.postValue(result.state)
        }.launchIn(viewModelScope)
    }

}
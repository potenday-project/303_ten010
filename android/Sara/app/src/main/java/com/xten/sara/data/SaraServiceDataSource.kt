package com.xten.sara.data

import android.content.SharedPreferences
import com.example.common.Resource
import com.xten.sara.data.model.ChatGPT
import com.xten.sara.data.model.Gallery
import com.xten.sara.data.model.Image
import com.xten.sara.util.LoginUtils
import com.xten.sara.data.model.Login
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-03-31
 * @desc
 */
class SaraServiceDataSource @Inject constructor(
    private val api: SaraServiceAPI,
    private val prefs: SharedPreferences
) {
    fun getToken(
        email: String,
        nickname: String?,
        profile: String?
    ): Flow<Resource<Login>> = flow {
        emit(Resource.Loading())
        try {
            val data = api.login(LoginRequestBody(email, nickname, profile)).body()
            emit(Resource.Success(data))
        } catch (e: Exception) {
            emit(Resource.Error(e.toString()))
        }
    }

    fun getImageUrl(file: File): Flow<Resource<Image>> = flow {
        emit(Resource.Loading())
        try {
            val auth = LoginUtils.getToken(prefs)
            val image = createMultipartBody(file)
            val data = api.getImageUrl(
                header = auth!!,
                image = image
            ).body()
            emit(Resource.Success(data))
        } catch (e: Exception){
            emit(Resource.Error(e.toString()))
        }
    }
    private fun createMultipartBody(file: File) = MultipartBody.Part.createFormData(
        PARAM_PHOTO,
        file.name,
        file.asRequestBody(CONTENT_TYPE_IMAGE.toMediaTypeOrNull())
    )

    fun requestChatGPT(
        url: String,
        type: Int,
        text: String? = null
    ): Flow<Resource<ChatGPT>> = flow {
        emit(Resource.Loading())
        val auth = LoginUtils.getToken(prefs)!!
        try {
            val result = api.requestChatGPT(
                header = auth,
                requestBody = ChatGPTRequestBody(url, type, text)
            ).body()
            emit(Resource.Success(result))
        } catch (e: Exception) {
            emit(Resource.Error(e.toString()))
        }
    }

    fun saveContent(
        photoUrl: String,
        title: String,
        text: String,
        type: Int
    ): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val auth = LoginUtils.getToken(prefs)!!
            api.saveContent(
                header = auth,
                requestBody = SaveRequestBody(photoUrl, title, text, type)
            )
            emit(Resource.Success(null))
        } catch (e: Exception) {
            emit(Resource.Error(e.toString()))
        }
    }

    fun getCollection(): Flow<Resource<List<Gallery>>> = flow {
        emit(Resource.Loading())
        try {
            val auth = LoginUtils.getToken(prefs)!!
            val data = api.getCollection(
                header = auth
            ).body()?.result
            emit(Resource.Success(data))
        } catch (e: Exception) {
            emit(Resource.Error(e.toString()))
        }
    }

    fun deleteContent(id: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val auth = LoginUtils.getToken(prefs)!!
            api.deleteContent(
                header = auth,
                id = id
            )
            emit(Resource.Success(null))
        } catch (e: Exception) {
            emit(Resource.Error(e.toString()))
        }
    }

    fun getMyCollection(): Flow<Resource<List<Gallery>>> = flow {
        emit(Resource.Loading())
        try {
            val auth = LoginUtils.getToken(prefs)!!
            val data = api.getMyCollection(
                header = auth
            ).body()?.result
            emit(Resource.Success(data))
        } catch (e: Exception) {
            emit(Resource.Error(e.toString()))
        }
    }

    companion object {
        const val PARAM_PHOTO = "photo"
        private const val CONTENT_TYPE_IMAGE = "image/*"
    }
}
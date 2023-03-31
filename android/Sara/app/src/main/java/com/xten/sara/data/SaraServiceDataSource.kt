package com.xten.sara.data

import android.content.SharedPreferences
import android.util.Log
import com.xten.sara.util.LoginUtils
import com.xten.sara.util.constants.State
import com.xten.sara.util.constants.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
    suspend fun getToken(
        email: String,
        nickname: String
    ) = withContext(Dispatchers.IO) {
        try {
            api.login(LoginRequestBody(email, nickname)).body()?.token
        } catch (e: Exception) {
            Log.e(TAG, "getToken: $e", )
            null
        }
    }.also {
        Log.e(TAG, "getToken: email: $email, nickname: $nickname", )
    }

    suspend fun getImageUrl(file: File) = withContext(Dispatchers.IO) {
        try {
            val auth = LoginUtils.getToken(prefs)
            val image = createMutipartBody(file)

            api.getImageUrl(
                header = auth!!,
                image = image
            ).body()
        } catch (e: Exception){
            Log.e(TAG, "getImageUrl: $e", )
            null
        }
    }
    private fun createMutipartBody(file: File) = MultipartBody.Part.createFormData(
        PARAM_PHOTO,
        file.name,
        file.asRequestBody(CONTENT_TYPE_IMAGE.toMediaTypeOrNull())
    )

    suspend fun requestChatGPT(
        url: String,
        type: Int,
        text: String?=null
    ) = withContext(Dispatchers.IO) {
            val auth = LoginUtils.getToken(prefs)!!
            try {
                val result = api.requestChatGPT(
                    header = auth,
                    requestBody = ChatGPTRequestBody(url, type, text)
                ).body()
                result?.let {
                    return@withContext it
                } ?: null
            } catch (e: Exception) {
                Log.e(TAG, "requestChatGPT: $e", )
                null
            }
        }

    suspend fun saveContent(
        photoUrl: String,
        title: String,
        text: String,
        type: Int
    ) = withContext(Dispatchers.IO) {
        try {
            val auth = LoginUtils.getToken(prefs)!!
            api.saveContent(
                header = auth,
                requestBody = SaveRequestBody(photoUrl, title, text, type)
            )
            State.SUCCESS.name
        } catch (e: Exception) {
            Log.e(TAG, "saveContent: $e", )
            State.FAIL.name
        }
    }

    suspend fun getCollection() = withContext(Dispatchers.IO) {
        try {
            val auth = LoginUtils.getToken(prefs)!!
            api.getCollection(
                header = auth
            ).body()?.result
        } catch (e: Exception) {
            Log.e(TAG, "getCollection: $e", )
            null
        }
    }

    suspend fun deleteContent(id: String) = withContext(Dispatchers.IO) {
        try {
            val auth = LoginUtils.getToken(prefs)!!
            api.deleteContent(
                header = auth,
                id = id
            )
            State.SUCCESS.name
        } catch (e: Exception) {
            Log.e(TAG, "deleteContent: $e", )
            State.FAIL.name
        }
    }

    suspend fun getMyCollection() = withContext(Dispatchers.IO) {
        try {
            val auth = LoginUtils.getToken(prefs)!!
            api.getMyCollection(
                header = auth
            ).body()?.result
        } catch (e: Exception) {
            Log.e(TAG, "getMyCollection: $e", )
            null
        }
    }

    companion object {
        const val PARAM_PHOTO = "photo"
        private const val CONTENT_TYPE_IMAGE = "image/*"
    }
}
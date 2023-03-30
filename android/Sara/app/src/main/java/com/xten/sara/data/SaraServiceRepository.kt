package com.xten.sara.data

import android.content.SharedPreferences
import com.xten.sara.util.LoginUtils
import com.xten.sara.util.State
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
 * @created 2023-03-30
 * @desc
 */
class SaraServiceRepository @Inject constructor(
    private val api: SaraServiceAPI,
    private val prefs: SharedPreferences
) {

    suspend fun getToken(email: String) = withContext(Dispatchers.IO) {
        try {
            return@withContext api.login(LoginRequestBody(email)).body()?.token
        } catch (e: Exception) {
            return@withContext null
        }
    }

    suspend fun getImageUrl(file: File) = withContext(Dispatchers.IO) {
        try {
            val auth = LoginUtils.getToken(prefs)!!
            val image = createMutipartBody(file)

            return@withContext api.getImageUrl(
                header = auth,
                image = image
            ).body()
        } catch (e: Exception){
            return@withContext null
        }
    }
    private fun createMutipartBody(file: File) = MultipartBody.Part.createFormData(
        PARAM_PHOTO,
        file.name,
        file.asRequestBody(CONTENT_TYPE_IMAGE.toMediaTypeOrNull())
    )

    suspend fun requestChatGPT(url: String, type: Int) =
        withContext(Dispatchers.IO) {
            val auth = LoginUtils.getToken(prefs)!!
            try {
                api.requestChatGPT(
                    header = auth,
                    requestBody = ChatGPTRequestBody(url, type)
                ).body()?.let {
                    return@withContext it
                } ?: null
            } catch (e: Exception) {
                return@withContext null
            }
        }

    suspend fun saveContent(url: String, text: String) = withContext(Dispatchers.IO) {
        try {
            val auth = LoginUtils.getToken(prefs)!!
            api.saveContent(
                header = auth,
                requestBody = SaveRequestBody(url, text)
            )
            return@withContext State.SUCCESS.name
        } catch (e: Exception) {
            return@withContext State.FAIL.name
        }
    }


    companion object {
        const val PARAM_PHOTO = "photo"
        private const val CONTENT_TYPE_IMAGE = "image/*"
    }

}


package com.xten.sara.data

import android.content.SharedPreferences
import android.util.Log
import com.xten.sara.util.LoginUtils
import com.xten.sara.util.State
import com.xten.sara.util.TAG
import kotlinx.coroutines.CoroutineScope
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
    private val saraServiceDataSource: SaraServiceDataSource
) {

    suspend fun downloadToken(email: String) = saraServiceDataSource.getToken(email)

    suspend fun downloadImageUrl(file: File) = saraServiceDataSource.getImageUrl(file)

    suspend fun downloadResultChatGPT(url: String, type: Int, text: String?=null) =
        saraServiceDataSource.requestChatGPT(url, type, text)

    suspend fun requestSaveContent(url: String, text: String) = saraServiceDataSource.saveContent(url, text)

    suspend fun downloadCollection() = saraServiceDataSource.getCollection()

    suspend fun requestDeleteContent(id: String) = saraServiceDataSource.deleteContent(id)

    suspend fun downloadMyCollection() = saraServiceDataSource.getMyCollection()

}


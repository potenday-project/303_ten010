package com.xten.sara.data

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

    suspend fun downloadToken(
        email: String,
        nickName: String?,
        profile: String?
    ) = saraServiceDataSource.getToken(email, nickName, profile)

    suspend fun downloadImageUrl(file: File) = saraServiceDataSource.getImageUrl(file)

    suspend fun downloadResultChatGPT(
        url: String,
        type: Int,
        text: String? = null
    ) = saraServiceDataSource.requestChatGPT(url, type, text)

    suspend fun requestSaveContent(
        photoUrl: String,
        title: String,
        text: String,
        type: Int
    ) = saraServiceDataSource.saveContent(photoUrl, title, text, type)

    suspend fun downloadCollection() = saraServiceDataSource.getCollection()

    suspend fun requestDeleteContent(id: String) = saraServiceDataSource.deleteContent(id)

    suspend fun downloadMyCollection() = saraServiceDataSource.getMyCollection()

}


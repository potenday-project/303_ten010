package com.xten.sara.data

import com.example.common.Resource
import com.xten.sara.data.model.Gallery
import com.xten.sara.data.model.Image
import com.xten.sara.data.model.Login
import kotlinx.coroutines.flow.Flow
import java.io.File
import javax.inject.Inject

class SaraServiceRepositoryImpl @Inject constructor(
    private val saraServiceDataSource: SaraServiceDataSource
) : SaraServiceRepository {
    override fun downloadToken(
        email: String,
        nickName: String?,
        profile: String?
    ): Flow<Resource<Login>> = saraServiceDataSource.getToken(email, nickName, profile)

    override fun downloadImageUrl(file: File): Flow<Resource<Image>> = saraServiceDataSource.getImageUrl(file)

    override fun downloadResultChatGPT(
        url: String,
        type: Int,
        text: String?
    ) = saraServiceDataSource.requestChatGPT(url, type, text)

    override fun requestSaveContent(
        photoUrl: String,
        title: String,
        text: String,
        type: Int
    ) = saraServiceDataSource.saveContent(photoUrl, title, text, type)

    override fun downloadCollection(): Flow<Resource<List<Gallery>>> = saraServiceDataSource.getCollection()

    override fun requestDeleteContent(id: String): Flow<Resource<String>> = saraServiceDataSource.deleteContent(id)

    override fun downloadMyCollection(): Flow<Resource<List<Gallery>>> = saraServiceDataSource.getMyCollection()

}
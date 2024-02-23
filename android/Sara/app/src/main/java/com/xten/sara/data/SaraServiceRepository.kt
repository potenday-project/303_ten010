package com.xten.sara.data

import com.example.common.Resource
import com.xten.sara.data.model.ChatGPT
import com.xten.sara.data.model.Gallery
import com.xten.sara.data.model.Image
import com.xten.sara.data.model.Login
import kotlinx.coroutines.flow.Flow
import java.io.File

interface SaraServiceRepository {

    fun downloadToken(
        email: String,
        nickName: String?,
        profile: String?
    ) : Flow<Resource<Login>>

    fun downloadImageUrl(file: File) : Flow<Resource<Image>>

    fun downloadResultChatGPT(
        url: String,
        type: Int,
        text: String? = null
    ) : Flow<Resource<ChatGPT>>

    fun requestSaveContent(
        photoUrl: String,
        title: String,
        text: String,
        type: Int
    ) : Flow<Resource<String>>

    fun downloadCollection() : Flow<Resource<List<Gallery>>>

    fun requestDeleteContent(id: String) : Flow<Resource<String>>

    fun downloadMyCollection() : Flow<Resource<List<Gallery>>>

}
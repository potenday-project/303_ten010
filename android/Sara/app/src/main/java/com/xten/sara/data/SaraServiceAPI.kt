package com.xten.sara.data

import com.example.common.AUTHORIZATION
import com.xten.sara.data.model.ChatGPT
import com.xten.sara.data.model.GalleryResponse
import com.xten.sara.data.model.Image
import com.xten.sara.data.model.Login
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-03-29
 * @desc
 */
interface SaraServiceAPI {
    @POST("user")
    suspend fun login(
        @Body requestBody: LoginRequestBody
    ) : Response<Login>

    @Multipart
    @POST("image")
    suspend fun getImageUrl(
        @Header(AUTHORIZATION) header: String,
        @Part image: MultipartBody.Part
    ) : Response<Image>

    @POST("chatgpt")
    suspend fun requestChatGPT(
        @Header(AUTHORIZATION) header: String,
        @Body requestBody: ChatGPTRequestBody
    ) : Response<ChatGPT>

    @POST("gallery")
    suspend fun saveContent(
        @Header(AUTHORIZATION) header: String,
        @Body requestBody: SaveRequestBody
    )

    @GET("gallery")
    suspend fun getCollection(
        @Header(AUTHORIZATION) header: String
    ) : Response<GalleryResponse>

    @DELETE("gallery/{id}")
    suspend fun deleteContent(
        @Header(AUTHORIZATION) header: String,
        @Path("id") id: String
    )

    @GET("gallery")
    suspend fun getMyCollection(
        @Header(AUTHORIZATION) header: String,
        @Query("type") type: String = "user"
    ) : Response<GalleryResponse>


}

data class LoginRequestBody(
    val email: String,
    val nickname: String?,
    val profile: String?
)

data class ChatGPTRequestBody(
    val photoUrl: String,
    val type: Int,
    val text: String? = null
)

data class SaveRequestBody(
    val photoUrl: String,
    val title: String,
    val text: String,
    val type: Int
)
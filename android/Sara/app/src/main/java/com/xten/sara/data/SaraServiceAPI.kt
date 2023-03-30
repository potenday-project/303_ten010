package com.xten.sara.data

import com.xten.sara.data.response.*
import com.xten.sara.util.AUTHORIZATION
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

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
    ) : Response<LoginResponse>

    @Multipart
    @POST("image")
    suspend fun getImageUrl(
        @Header(AUTHORIZATION) header: String,
        @Part image: MultipartBody.Part
    ) : Response<ImageResponse>

    @POST("chatgpt")
    suspend fun requestChatGPT(
        @Header(AUTHORIZATION) header: String,
        @Body requestBody: ChatGPTRequestBody
    ) : Response<ChatGPTResponse>

    @POST("gallery")
    suspend fun saveContent(
        @Header(AUTHORIZATION) header: String,
        @Body requestBody: SaveRequestBody
    )

}

data class LoginRequestBody(
    val email: String
)

data class ChatGPTRequestBody(
    val photoUrl: String,
    val type: Int
)

data class SaveRequestBody(
    val photoUrl: String,
    val text: String
)
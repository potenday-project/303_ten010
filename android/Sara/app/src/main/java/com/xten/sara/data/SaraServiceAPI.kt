package com.xten.sara.data

import com.xten.sara.util.AUTHORIZATION
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

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
package com.xten.sara.data

import com.xten.sara.data.response.ImageTaggingResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-03-29
 * @desc
 */
interface ImageTaggingServiceAPI {
    @Multipart
    @POST("uploads")
    suspend fun getImageTagging(
        @Part image: MultipartBody.Part
    ) : Response<ImageTaggingResponse>
}
package com.xten.sara.data

import com.xten.sara.data.response.LoginResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-03-29
 * @desc
 */
interface SaraServiceAPI {
    @POST("user")
    suspend fun login(
        @Body email: LoginRequestBody
    ) : Response<LoginResponse>

}

data class LoginRequestBody(
    val email: String
)
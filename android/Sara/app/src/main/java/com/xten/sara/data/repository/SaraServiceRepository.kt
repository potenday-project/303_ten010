package com.xten.sara.data.repository

import com.xten.sara.data.LoginRequestBody
import com.xten.sara.data.SaraServiceAPI
import com.xten.sara.data.response.LoginResponse
import retrofit2.Response
import javax.inject.Inject

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-03-30
 * @desc
 */
class SaraServiceRepository @Inject constructor(
    private val api: SaraServiceAPI
) {

    suspend fun login(email: String) = run {
        api.login(LoginRequestBody(email))
    }
}
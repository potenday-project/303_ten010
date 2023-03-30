package com.xten.sara.data.response

import com.google.gson.annotations.SerializedName

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-03-30
 * @desc
 */

data class LoginResponse(
    @SerializedName("token")
    val token: String
)

data class ImageResponse(
    @SerializedName("photoUrl")
    val photoUrl: String
)

data class ChatGPTResponse(
    @SerializedName("text")
    val text: String
)
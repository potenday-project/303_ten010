package com.xten.sara.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-03-30
 * @desc
 */

data class Login(
    @SerializedName("token")
    val token: String
)

data class Image(
    @SerializedName("photoUrl")
    val photoUrl: String
)

data class ChatGPT(
    @SerializedName("text")
    val text: String
)

data class GalleryResponse(
    @SerializedName("result")
    val result: List<Gallery>,
    @SerializedName("count")
    val cont: Int
)

data class Gallery (
    @SerializedName("_id")
    val _id: String?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("nickname")
    val nickname: String?,
    @SerializedName("photoUrl")
    val photoUrl: String?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("text")
    val text: String?,
    @SerializedName("type")
    val type: Int?,
    @SerializedName("createdAt")
    val createdAt: Date?
) : Serializable


package com.xten.sara.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.Date

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
    @SerializedName("profile")
    val profile: String?,
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
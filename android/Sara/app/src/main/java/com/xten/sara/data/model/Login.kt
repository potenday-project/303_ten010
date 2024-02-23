package com.xten.sara.data.model

import com.google.gson.annotations.SerializedName

data class Login(
    @SerializedName("token")
    val token: String
)

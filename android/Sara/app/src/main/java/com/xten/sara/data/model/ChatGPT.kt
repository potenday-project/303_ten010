package com.xten.sara.data.model

import com.google.gson.annotations.SerializedName

data class ChatGPT(
    @SerializedName("text")
    val text: String
)
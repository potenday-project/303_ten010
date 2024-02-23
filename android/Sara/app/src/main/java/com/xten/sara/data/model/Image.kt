package com.xten.sara.data.model

import com.google.gson.annotations.SerializedName

data class Image(
    @SerializedName("photoUrl")
    val photoUrl: String
)
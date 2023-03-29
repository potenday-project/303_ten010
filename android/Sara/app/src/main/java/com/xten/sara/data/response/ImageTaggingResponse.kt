package com.xten.sara.data.response

import com.google.gson.annotations.SerializedName

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-03-29
 * @desc
 */
data class ImageTaggingResponse(
    @SerializedName("result")
    val result: TaggingResult,
)
data class TaggingResult(
    @SerializedName("upload_id")
    val uploadId: String // i05e132196706b94b1d85efb5f3SaM1j
)

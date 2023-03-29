package com.xten.sara.util

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-03-29
 * @desc
 */
object DataUtils {

    fun getMutipartBody(file: File) = MultipartBody.Part.createFormData(
        PARAM_IMAGE,
        file.name,
        file.asRequestBody(CONTENT_TYPE_IMAGE.toMediaTypeOrNull())
    )

}

private const val CONTENT_TYPE_IMAGE = "image/*"
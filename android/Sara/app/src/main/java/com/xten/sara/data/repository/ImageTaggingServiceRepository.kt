package com.xten.sara.data.repository

import com.xten.sara.data.ImageTaggingServiceAPI
import com.xten.sara.util.DataUtils
import okhttp3.MultipartBody
import java.io.File
import javax.inject.Inject

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-03-29
 * @desc
 */
class ImageTaggingServiceRepository @Inject constructor(
    private val api: ImageTaggingServiceAPI
) {
    suspend fun getUploadId(image: File) = run {
        val image = DataUtils.getMutipartBody(image)
        api.getImageTagging(image)
    }
}
package com.xten.sara.extensions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import com.xten.sara.util.ImageFileUtils
import java.io.File

inline fun Intent.putExtraCameraUri(context: Context, handleImageUri: (Uri?)-> Unit) : File? {
    return resolveActivity(context.packageManager)?.run {
        val cacheFile = ImageFileUtils.createCacheTempFile(context)
        val uri = ImageFileUtils.getCacheTempFileUri(context, cacheFile)
        handleImageUri(uri)
        putExtra(MediaStore.EXTRA_OUTPUT, uri)
        return cacheFile
    }
}

fun Intent.createShareIntent(str: String, SHARE_TITLE_TEXT: String = "친구에게 공유하기"): Intent = apply {
    action = Intent.ACTION_SEND
    type = "text/*"
    putExtra(Intent.EXTRA_TEXT, str)
    putExtra(Intent.EXTRA_TITLE, SHARE_TITLE_TEXT)
    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
}


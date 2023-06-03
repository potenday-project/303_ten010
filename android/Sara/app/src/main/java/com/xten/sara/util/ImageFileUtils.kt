package com.xten.sara.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import androidx.core.content.FileProvider
import java.io.File

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-03-29
 * @desc
 */


object ImageFileUtils {

    fun createCacheTempFile(context: Context): File? = File.createTempFile (
        TEMP_FILE_PREFIX,
        TEMP_FILE_SUFFIX,
        context.cacheDir
    )
    fun getCacheTempFileUri(context: Context, file: File?) = file?.run {
        FileProvider.getUriForFile(
            context,
            context.packageName,
            file
        )
    }

    fun createFileAccessSettingsIntent (context: Context) : Intent =
        Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
            addCategory(ANDROID_INTENT_CATEGORY_DEFAULT)
            data = Uri.parse(String.format(FORMAT_PACKAGE, context.packageName))
    }
    fun createChooserIntent(cameraIntent: Intent): Intent? {
        val galleryIntent = Intent(Intent.ACTION_PICK).apply {
            type = MediaStore.Images.Media.CONTENT_TYPE
        }
        return Intent.createChooser(Intent(), CHOOSER_TITLE).apply {
            putExtra(Intent.EXTRA_INTENT, galleryIntent)
            putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(cameraIntent))
        }
    }

    fun getAbsolutePath(context: Context, uri: Uri) : String {
        var path = uri.toString()
        try {
            context.contentResolver.query(
                uri, arrayOf(MediaStore.Images.Media.DATA), null, null, null
            )?.let {
                val index = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                it.moveToFirst()
                path = it.getString(index)
            }
        } catch (e: Exception) {
            path = getTempFileAbsolutePath(context, path)
        }
        return path
    }

    private fun getTempFileAbsolutePath(context: Context, uri: String): String {
        val split = uri.replace("content://${context.packageName}/images/", "")
        return "/data/data/${context.packageName}/cache/$split"
    }


}

private const val TEMP_FILE_PREFIX = "temp"
private const val TEMP_FILE_SUFFIX = ".jpg"
private const val CHOOSER_TITLE = "사진을 가져올 방법을 선택하세요."
private const val ANDROID_INTENT_CATEGORY_DEFAULT = "android.intent.category.DEFAULT"
private const val FORMAT_PACKAGE = "package:%s"
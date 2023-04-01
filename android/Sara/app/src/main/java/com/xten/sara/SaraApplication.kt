package com.xten.sara

import android.app.Activity
import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import com.xten.sara.util.constants.APP_NAME
import com.xten.sara.util.constants.MESSAGE_TEXT_COPY
import dagger.hilt.android.HiltAndroidApp


/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-03-28
 * @desc
 */

@HiltAndroidApp
class SaraApplication : Application() {
    companion object {
        fun showToast(context: Context, message: String) = Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

        fun dropdownSoftKeyboard(activity: Activity, inputManager: InputMethodManager) = activity.currentFocus?.let{
            inputManager.hideSoftInputFromWindow(
                it.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
            it.clearFocus()
        }

        fun copyToClipboard(context: Context, clipboardManager: ClipboardManager, text: String) {
            val clipData = ClipData.newPlainText(
                APP_NAME,
                text
            )
            clipboardManager.setPrimaryClip(clipData)
            showToast(context, MESSAGE_TEXT_COPY)
        }
    }
}
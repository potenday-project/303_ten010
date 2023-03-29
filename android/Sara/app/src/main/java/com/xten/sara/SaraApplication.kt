package com.xten.sara

import android.app.Activity
import android.app.Application
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
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
    }
}
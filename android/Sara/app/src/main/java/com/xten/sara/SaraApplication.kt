package com.xten.sara

import android.app.Activity
import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
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
class SaraApplication : Application()
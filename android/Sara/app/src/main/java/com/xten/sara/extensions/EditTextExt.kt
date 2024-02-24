package com.xten.sara.extensions

import android.app.Activity
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

const val MAX_TEXT_TITLE_LENGTH = 20


private const val MAX_TEXT_LENGTH = 30
private val TEXT_FIELD_ERROR_MESSAGE : (Int) -> (String) = { num -> "최대 ${num}글자를 넘을 수 없습니다." }
fun TextInputEditText.connectWithTextField(textField: TextInputLayout, maxLength: Int = MAX_TEXT_LENGTH) {
    setOnFocusChangeListener { _, hasFocus ->
        textField.isHintEnabled = hasFocus
    }
    doAfterTextChanged {
        it?.run { if(length >= maxLength) textField.error = TEXT_FIELD_ERROR_MESSAGE(maxLength) }
    }
}

inline fun TextInputEditText.setEnterKeyEvent(crossinline enterKeyPressedCallback: ()-> Unit) {
    editableText.clear()
    setOnKeyListener { _, keyCode, _ ->
        when(keyCode) {
            KeyEvent.KEYCODE_ENTER -> {
                enterKeyPressedCallback()
                return@setOnKeyListener true
            }
            else -> return@setOnKeyListener false
        }
    }
}

fun InputMethodManager.dropDownSoftKeyboard(activity: Activity?) = activity?.currentFocus?.let{
    hideSoftInputFromWindow(
        it.windowToken,
        InputMethodManager.HIDE_NOT_ALWAYS
    )
    it.clearFocus()
}
package com.xten.sara.util

import android.content.SharedPreferences

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-03-30
 * @desc
 */
object LoginUtils {

    fun setLoginState(prefs: SharedPreferences, isChecked: Boolean) = prefs.edit()
        .putBoolean(LOGIN_STATE, isChecked)
        .apply()
    fun getLoginState(prefs: SharedPreferences) = prefs.getBoolean(LOGIN_STATE, false)

    fun saveToken(prefs: SharedPreferences, token: String) = prefs.edit()
        .putString(TOKEN, token)
        .apply()

    fun getToken(prefs: SharedPreferences) = prefs.getString(TOKEN, null)

    fun clearToken(prefs: SharedPreferences) = prefs.edit().clear()

}

private const val TOKEN = "token"
private const val LOGIN_STATE = "login_state"
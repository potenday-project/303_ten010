package com.xten.sara.util.di

import android.app.Application
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.view.inputmethod.InputMethodManager
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.gson.GsonBuilder
import com.xten.sara.data.SaraServiceAPI
import com.xten.sara.data.SaraServiceDataSource
import com.xten.sara.util.*
import com.xten.sara.util.constants.CONTENT_TYPE
import com.xten.sara.util.constants.SARA_BASE_URL
import com.xten.sara.util.constants.SARA_PREFS
import com.xten.sara.util.constants.TIME_OUT
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-03-28
 * @desc
 */

@Module
@InstallIn(SingletonComponent::class)
object AppModules {

    @Singleton
    @Provides
    fun provideAppPreferences(@ApplicationContext app: Context) = app.getSharedPreferences(
        SARA_PREFS, Context.MODE_PRIVATE
    )

    @Singleton
    @Provides
    fun provideGoogleSignInOptions() = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .build()

    @Singleton
    @Provides
    fun provideInputManager(@ApplicationContext app: Context) =
        app.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    @Singleton
    @Provides
    fun provideClipboardManager(@ApplicationContext app: Context) =
        app.getSystemService(Application.CLIPBOARD_SERVICE) as ClipboardManager

    @Singleton
    @Provides
    fun provideSaraAPIService() = run {
        val requestInterceptor = Interceptor {
            val url = it.request()
                .url
                .newBuilder()
                .build()
            val request = it.request()
                .newBuilder()
                .header(CONTENT_TYPE, "application/json")
                .url(url)
                .build()
            return@Interceptor it.proceed(request)
        }
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(requestInterceptor)
            .callTimeout(TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(TIME_OUT, TimeUnit.SECONDS)
            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
            .build()
        val gson = GsonBuilder()
            .setDateFormat("MMMM yyyy HH:mm:ss X")
            .setLenient()
            .create()
        Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(SARA_BASE_URL)
            .build()
            .create(SaraServiceAPI::class.java)
    }

    @Singleton
    @Provides
    fun provideSaraServiceDataSource(api: SaraServiceAPI, pref: SharedPreferences) =
        SaraServiceDataSource(api, pref)

}
package com.xten.sara.util.di

import android.content.Context
import android.view.inputmethod.InputMethodManager
import com.google.gson.GsonBuilder
import com.xten.sara.data.ImageTaggingServiceAPI
import com.xten.sara.data.SaraServiceAPI
import com.xten.sara.util.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.nio.charset.StandardCharsets
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
    fun provideInputManager(@ApplicationContext app: Context) =
        app.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    @Singleton
    @Provides
    fun provideImageTaggingAPIService() = run {
        val requestInterceptor = Interceptor {
            val url = it.request()
                .url
                .newBuilder()
                .build()
            val auth = Base64.getEncoder().encodeToString("$API_KEY:$API_SECRET".toByteArray(StandardCharsets.UTF_8))
            val request = it.request()
                .newBuilder()
                .header(AUTHORIZATION, "Basic $auth")
                .url(url)
                .build()
            return@Interceptor it.proceed(request)
        }
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(requestInterceptor)
            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
            .build()
        val gson = GsonBuilder()
            .setLenient()
            .create()
        Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(IMAGE_TAGGING_SERVICE_BASE_URL)
            .build()
            .create(ImageTaggingServiceAPI::class.java)
    }

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
                .url(url)
                .build()
            return@Interceptor it.proceed(request)
        }
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(requestInterceptor)
            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
            .build()
        val gson = GsonBuilder()
            .setLenient()
            .create()
        Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(SARA_BASE_URL)
            .build()
            .create(SaraServiceAPI::class.java)
    }

}
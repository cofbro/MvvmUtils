package com.cofbro.mvvmutils.base

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * 用来构建Retrofit，全局唯一 retrofit，允许重试，不允许重定向
 */
object RetrofitCreator {

    private val mOkClient = OkHttpClient.Builder()
        .callTimeout(Config.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
        .connectTimeout(Config.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
        .readTimeout(Config.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
        .writeTimeout(Config.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
        .retryOnConnectionFailure(true)
        .followRedirects(false)
        .build()

    private fun getRetrofitBuilder(baseUrl: String): Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(mOkClient)
            .addConverterFactory(GsonConverterFactory.create())
    }

    /**
     * Create Api service
     *  @param  cls Api Service
     *  @param  baseUrl Base Url
     */
    fun <T> getApiService(cls: Class<T>, baseUrl: String): T {
        val retrofit = getRetrofitBuilder(
            baseUrl
        ).build()
        return retrofit.create(cls)
    }

    /**
     * okhttp相关参数
     */
    class Config {
        companion object {
            const val DEFAULT_TIMEOUT: Long = 10000L
        }

    }
}
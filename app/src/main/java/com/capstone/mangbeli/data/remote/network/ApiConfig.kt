package com.capstone.mangbeli.data.remote.network

import android.util.Log
import com.capstone.mangbeli.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {

    companion object {
        private var refreshTokenCallback: ((String, String) -> Unit)? = null

        fun setRefreshTokenCallback(callback: (String, String) -> Unit) {
            refreshTokenCallback = callback
        }

        fun getApiService(token: String): ApiService {
            val loggingInterceptor =
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            val authInterceptor = Interceptor { chain ->
                val req = chain.request()
                val response = chain.proceed(req)
                val requestHeaders = req.newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                val cookies = response.headers("Set-Cookie")
                cookies.forEach { cookie ->
                    if (cookie.contains("refreshToken")) {
                        val refreshToken =
                            cookie.substringAfter("refreshToken=").substringBefore(";")
                        Log.d("RefreshToken", "Refresh Token: $refreshToken")
                        val Expired = cookie.substringAfter("Expires=").substringBefore(";")
                        refreshTokenCallback?.invoke(refreshToken, Expired)

                    }
                }
                response.close()
                chain.proceed(requestHeaders)
            }
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authInterceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit.create(ApiService::class.java)
        }

        fun getApiServicee(): ApiService {
            val loggingInterceptor =
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit.create(ApiService::class.java)
        }
        fun getApiService2(refreshToken: String): ApiService {
            val loggingInterceptor =
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            val authInterceptor = Interceptor { chain ->
                val req = chain.request()
                val requestHeaders = req.newBuilder()
                    .addHeader("Cookie", "refreshToken=$refreshToken")
                    .build()
                chain.proceed(requestHeaders)
            }
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authInterceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit.create(ApiService::class.java)
        }

        private const val BASE_URL = BuildConfig.BASE_URL
    }
}


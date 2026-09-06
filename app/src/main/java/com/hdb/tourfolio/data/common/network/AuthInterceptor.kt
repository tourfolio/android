package com.hdb.tourfolio.data.common.network

import android.util.Log
import com.hdb.tourfolio.data.auth.local.SessionLocalDataSource
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation
import javax.inject.Inject

private const val TAG = "AuthInterceptor"

class AuthInterceptor
    @Inject
    constructor(
        private val sessionLocalDataSource: SessionLocalDataSource,
    ) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()

            val requiresAuth =
                request
                    .tag(Invocation::class.java)
                    ?.method()
                    ?.isAnnotationPresent(Authenticated::class.java) == true

            if (!requiresAuth) return chain.proceed(request)

            val token = runBlocking { sessionLocalDataSource.getSession()?.token }
            if (token == null) {
                Log.d(TAG, "${request.url} requires auth but no session token is stored")
                return chain.proceed(request)
            }

            val headerValue = "TOKEN_$token"
            Log.d(TAG, "${request.url} -> Authorization: $headerValue")

            val authorizedRequest =
                request
                    .newBuilder()
                    .addHeader("Authorization", headerValue)
                    .build()

            return chain.proceed(authorizedRequest)
        }
    }
